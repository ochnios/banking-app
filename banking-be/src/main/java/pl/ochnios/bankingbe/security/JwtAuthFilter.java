package pl.ochnios.bankingbe.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.web.filter.OncePerRequestFilter;
import pl.ochnios.bankingbe.services.SecurityService;

import java.io.IOException;

public class JwtAuthFilter extends OncePerRequestFilter {

    @Autowired
    private SecurityService securityService;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {

        securityService.findAccessToken(request)
                .filter(securityService::validateAccessToken)
                .ifPresent(accessToken -> {
                    String refreshedAccessToken = securityService.authenticateWithAccessToken(accessToken);
                    securityService.setAccessToken(response, refreshedAccessToken);
                });

        filterChain.doFilter(request, response);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        return request.getServletPath().startsWith("/api/auth");
    }
}
