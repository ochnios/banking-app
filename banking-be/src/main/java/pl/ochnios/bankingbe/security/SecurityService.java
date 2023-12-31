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

import java.util.Arrays;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SecurityService {

    private final AuthenticationManager authManager;

    public String getCurrentUserId() {
        return ((User) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getId().toString();
    }

    public void authenticateUser(LoginDto loginDto) {
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                loginDto.getUsername(), loginDto.getPassword()
        );
        Authentication auth = authManager.authenticate(authToken);
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    public void authenticateUser(User user) {
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                user, null, user.getAuthorities()
        );
        SecurityContextHolder.getContext().setAuthentication(authToken);
    }

    public Optional<String> getAccessToken(HttpServletRequest request) {
        return Arrays.stream(Optional.ofNullable(request.getCookies()).orElse(new Cookie[]{}))
                .filter(cookie -> SecurityConf.AUTH_COOKIE_NAME.equals(cookie.getName()))
                .map(Cookie::getValue)
                .findFirst();
    }

    public void setAccessToken(HttpServletResponse response, String jwt) {
        response.addCookie(generateAuthCookie(jwt));
    }

    private Cookie generateAuthCookie(String jwt) {
        Cookie authCookie = new Cookie(SecurityConf.AUTH_COOKIE_NAME, jwt);
        authCookie.setMaxAge(SecurityConf.JWT_EXPIRATION_MS / 1000);
        authCookie.setHttpOnly(true);
        authCookie.setSecure(false); // TEMP
        return authCookie;
    }
}
