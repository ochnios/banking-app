package pl.ochnios.bankingbe.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;
import pl.ochnios.bankingbe.model.entities.User;
import pl.ochnios.bankingbe.services.UserService;

import java.io.IOException;
import java.util.Arrays;
import java.util.Optional;

public class JwtAuthFilter extends OncePerRequestFilter {

    @Autowired
    private JwtProvider jwtProvider;
    @Autowired
    private UserService userService;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {

        getAccessTokenFromCookie(request)
                .filter(jwtProvider::validateJwt)
                .ifPresent(token -> {
                    User user = userService.getUserById(jwtProvider.getUserIdFromJwt(token));
                    authenticateUser(user);
                    refreshToken(user, response);
                });

        filterChain.doFilter(request, response);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        return request.getServletPath().startsWith("/api/auth");
    }

    private void authenticateUser(User user) {
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                user, null, user.getAuthorities()
        );
        SecurityContextHolder.getContext().setAuthentication(authToken);
    }

    private void refreshToken(User user, HttpServletResponse response) {
        String refreshedToken = jwtProvider.generateJwtForUser(user);
        response.addCookie(generateAuthCookie(refreshedToken));
    }

    public static Cookie generateAuthCookie(String token) {
        Cookie authCookie = new Cookie(SecurityConf.AUTH_COOKIE_NAME, token);
        authCookie.setMaxAge(SecurityConf.JWT_EXPIRATION_MS / 1000);
        authCookie.setHttpOnly(true);
        authCookie.setSecure(false); // TEMP
        return authCookie;
    }

    private Optional<String> getAccessTokenFromCookie(HttpServletRequest request) {
        return Arrays.stream(Optional.ofNullable(request.getCookies()).orElse(new Cookie[]{}))
                .filter(cookie -> SecurityConf.AUTH_COOKIE_NAME.equals(cookie.getName()))
                .map(Cookie::getValue)
                .findFirst();
    }
}
