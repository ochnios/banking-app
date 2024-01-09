package pl.ochnios.bankingbe.security;

import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import pl.ochnios.bankingbe.exceptions.BlockedAccountException;
import pl.ochnios.bankingbe.model.dtos.input.LoginDto;
import pl.ochnios.bankingbe.model.dtos.output.UserDto;
import pl.ochnios.bankingbe.model.entities.User;
import pl.ochnios.bankingbe.model.entities.UserStatus;
import pl.ochnios.bankingbe.model.mappers.UserMapper;
import pl.ochnios.bankingbe.repositories.UserRepository;
import pl.ochnios.bankingbe.services.PasswordService;

import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SecurityService {

    private final AuthenticationManager authManager;
    private final JwtProvider jwtProvider;
    private final PasswordService passwordService;
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public String getAuthenticatedUserId() {
        return ((User) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getId().toString();
    }

    public UserDto getAuthenticatedUser() {
        return userMapper.map((User) SecurityContextHolder.getContext().getAuthentication().getPrincipal());
    }

    public String authenticateWithCredentials(LoginDto loginDto) {
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                loginDto.getUsername(), loginDto.getPassword());
        Authentication auth = authManager.authenticate(authToken);
        SecurityContextHolder.getContext().setAuthentication(auth);

        return jwtProvider.generateJwtForUser((User) auth.getPrincipal());
    }

    public String authenticateWithPartialPassword(LoginDto loginDto) {
        User authUser = findUserByUsername(loginDto.getUsername());
        if (passwordService.verifyPartialPassword(authUser.getPasswordEntity(), loginDto.getPassword())) {
            handleSuccessfulAuthentication(authUser);
            return jwtProvider.generateJwtForUser(authUser);
        } else {
            handleFailedAuthentication(authUser);
            throw new BadCredentialsException("Partial password is incorrect");
        }
    }

    public String authenticateWithAccessToken(String jwt) {
        User authUser = findUserById(jwtProvider.getUserIdFromJwt(jwt));
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                authUser, null, authUser.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authToken);
        return jwtProvider.generateJwtForUser(authUser);
    }

    public boolean validateAccessToken(String jwt) {
        return jwtProvider.validateJwt(jwt);
    }

    public Optional<String> findAccessToken(HttpServletRequest request) {
        return Arrays.stream(Optional.ofNullable(request.getCookies()).orElse(new Cookie[]{}))
                .filter(cookie -> SecurityConf.AUTH_COOKIE_NAME.equals(cookie.getName()))
                .map(Cookie::getValue)
                .findFirst();
    }

    public void setAccessToken(HttpServletResponse response, String jwt) {
        response.addCookie(generateAuthCookie(jwt, SecurityConf.JWT_EXPIRATION_MS / 1000));
    }

    public void removeAccessToken(HttpServletResponse response) {
        response.addCookie(generateAuthCookie("", 0));
    }

    private void handleSuccessfulAuthentication(User authUser) {
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                authUser, null, authUser.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authToken);

        authUser.setLoginAttempts(0);
        passwordService.resetPositions(authUser.getPasswordEntity());
        userRepository.save(authUser);
    }

    private void handleFailedAuthentication(User authUser) {
        if (authUser.getLoginAttempts() >= 3) {
            authUser.setStatus(UserStatus.BLOCKED);
        }
        authUser.setLoginAttempts(authUser.getLoginAttempts() + 1);
        userRepository.save(authUser);
    }

    private Cookie generateAuthCookie(String jwt, int maxAge) {
        Cookie authCookie = new Cookie(SecurityConf.AUTH_COOKIE_NAME, jwt);
        authCookie.setMaxAge(maxAge);
        authCookie.setHttpOnly(true);
        authCookie.setSecure(false); // TEMP
        return authCookie;
    }

    private User findUserById(String userId) {
        User user = userRepository.findById(UUID.fromString(userId))
                .orElseThrow(() -> new EntityNotFoundException(String.format("Can't find user with id=%s to authenticate", userId)));

        if (user.getStatus() != UserStatus.ACTIVE) {
            throw new BlockedAccountException(String.format("Account with id=%s is blocked", userId));
        }

        return user;
    }

    private User findUserByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Can't find user with username=%s to authenticate", username)));

        if (user.getStatus() != UserStatus.ACTIVE) {
            throw new BlockedAccountException(String.format("Account with username=%s is blocked", username));
        }

        return user;
    }
}