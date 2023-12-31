package pl.ochnios.bankingbe.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.web.filter.OncePerRequestFilter;
import pl.ochnios.bankingbe.model.entities.User;
import pl.ochnios.bankingbe.services.UserService;

import java.io.IOException;

public class JwtAuthFilter extends OncePerRequestFilter {

    @Autowired
    private SecurityService securityService;
    @Autowired
    private JwtProvider jwtProvider;
    @Autowired
    private UserService userService;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {

        securityService.getAccessToken(request)
                .filter(jwtProvider::validateJwt)
                .ifPresent(token -> {
                    User authUser = userService.getUserById(jwtProvider.getUserIdFromJwt(token));
                    securityService.authenticateUser(authUser);
                    securityService.setAccessToken(response, jwtProvider.generateJwtForUser(authUser));
                });

        filterChain.doFilter(request, response);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        return request.getServletPath().startsWith("/api/auth");
    }
}
