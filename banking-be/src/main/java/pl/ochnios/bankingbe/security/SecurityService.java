package pl.ochnios.bankingbe.security;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import pl.ochnios.bankingbe.model.dtos.LoginDto;
import pl.ochnios.bankingbe.model.entities.User;
import pl.ochnios.bankingbe.repositories.UserRepository;

import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SecurityService {

    private final AuthenticationManager authManager;
    private final UserRepository userRepository;
    private final JwtProvider jwtProvider;

    public String getCurrentUserId() {
        return ((User) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getId().toString();
    }

    public String authenticateWithCredentials(LoginDto loginDto) {
        return authenticate(loginDto.getUsername(), userRepository.findByUsername(loginDto.getUsername()), loginDto.getPassword());
    }

    public String authenticateWithAccessToken(String jwt) {
        UUID userId = UUID.fromString(jwtProvider.getUserIdFromJwt(jwt));
        return authenticate("", userRepository.findById(userId), null);
    }

    private String authenticate(String username, Optional<User> userOpt, String password) {
        Authentication auth = authManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
        SecurityContextHolder.getContext().setAuthentication(auth);

        User authUser = userOpt
                .orElseThrow(() -> new RuntimeException(String.format("Can't find user '%s' to authenticate", username)));
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

    private Cookie generateAuthCookie(String jwt, int maxAge) {
        Cookie authCookie = new Cookie(SecurityConf.AUTH_COOKIE_NAME, jwt);
        authCookie.setMaxAge(maxAge);
        authCookie.setHttpOnly(true);
        authCookie.setSecure(false); // TEMP
        return authCookie;
    }
}