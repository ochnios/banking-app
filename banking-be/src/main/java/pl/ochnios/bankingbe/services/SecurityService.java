package pl.ochnios.bankingbe.services;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import pl.ochnios.bankingbe.exceptions.BlockedAccountException;
import pl.ochnios.bankingbe.model.dtos.input.LoginDto;
import pl.ochnios.bankingbe.model.dtos.output.UserDto;
import pl.ochnios.bankingbe.model.entities.User;
import pl.ochnios.bankingbe.model.entities.UserStatus;
import pl.ochnios.bankingbe.model.mappers.UserMapper;
import pl.ochnios.bankingbe.security.JwtProvider;
import pl.ochnios.bankingbe.security.SecurityConf;

import java.util.Arrays;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SecurityService {

    private final JwtProvider jwtProvider;
    private final PasswordService passwordService;
    private final UserService userService;
    private final UserMapper userMapper;

    public String getAuthenticatedUserId() {
        return ((User) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getId().toString();
    }

    public UserDto getAuthenticatedUser() {
        return userMapper.map((User) SecurityContextHolder.getContext().getAuthentication().getPrincipal());
    }

    public String authenticateWithPartialPassword(LoginDto loginDto) {
        User authUser = userService.getUserEntityByUsername(loginDto.getUsername());
        if (passwordService.verifyPartialPassword(authUser.getPasswordEntity(), loginDto.getPassword())) {
            handleSuccessfulAuthentication(authUser);
            return jwtProvider.generateJwtForUser(authUser);
        } else {
            handleFailedAuthentication(authUser);
            throw new BadCredentialsException("Partial password is incorrect");
        }
    }

    public String authenticateWithAccessToken(String jwt) {
        User authUser = userService.getUserEntityById(jwtProvider.getUserIdFromJwt(jwt));
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
                //.filter(Cookie::isHttpOnly) TEMP
                .map(Cookie::getValue)
                .findFirst();
    }

    public void setAccessToken(HttpServletResponse response, String jwt) {
        response.addCookie(generateAuthCookie(jwt, SecurityConf.JWT_EXPIRATION_MS / 1000));
    }

    public void removeAccessToken(HttpServletResponse response) {
        response.addCookie(generateAuthCookie("", 0));
    }

    public int[] getPartialPasswordPositions(String username) {
        User user = userService.getUserEntityByUsername(username);
        return user.getPasswordEntity().getCurrentPositions();
    }

    public int[] fakePartialPasswordPositions(String username) {
        return passwordService.fakePartialPasswordPositions(username);
    }

    public void delayOperation() {
        delayOperation(SecurityConf.PUBLIC_ENDPOINTS_DELAY);
    }

    public void delayOperation(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
        }
    }

    private void handleSuccessfulAuthentication(User authUser) {
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                authUser, null, authUser.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authToken);

        authUser.setLoginAttempts(0);
        passwordService.resetPositions(authUser.getPasswordEntity());
        userService.saveUser(authUser);
    }

    private void handleFailedAuthentication(User authUser) {
        if (authUser.getLoginAttempts() >= 3) {
            authUser.setStatus(UserStatus.BLOCKED);
            userService.saveUser(authUser);
            throw new BlockedAccountException("Account blocked after 3 failed login attempts.");
        }
        authUser.setLoginAttempts(authUser.getLoginAttempts() + 1);
        userService.saveUser(authUser);
    }

    private Cookie generateAuthCookie(String jwt, int maxAge) {
        Cookie authCookie = new Cookie(SecurityConf.AUTH_COOKIE_NAME, jwt);
        authCookie.setMaxAge(maxAge);
        authCookie.setHttpOnly(true);
        authCookie.setPath("/");
        authCookie.setSecure(true);
        return authCookie;
    }
}