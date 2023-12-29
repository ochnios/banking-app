package pl.ochnios.bankingbe.security;

import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;
import pl.ochnios.bankingbe.model.entities.User;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class JwtProvider {

    private static final SecretKey secret = Jwts.SIG.HS512.key().build();
    private static final int JWT_EXPIRATION = 5 * 60 * 1000;

    public String generateJwtForUser(User user) {
        Date currentDate = new Date();
        Date expirationDate = new Date(currentDate.getTime() + JWT_EXPIRATION);
        JwtBuilder jwt = Jwts.builder()
                .issuedAt(currentDate)
                .expiration(expirationDate)
                .subject(user.getId().toString())
                .claim("username", user.getUsername());

        for (GrantedAuthority authority : user.getAuthorities()) {
            jwt.claim(authority.getAuthority(), true);
        }

        return jwt.signWith(secret)
                .compact();
    }

    public String getUserIdFromJwt(String jwt) {
        return Jwts.parser()
                .verifyWith(secret)
                .build()
                .parseSignedClaims(jwt)
                .getPayload()
                .getSubject();
    }

    public boolean validateJwt(String jwt) {
        try {
            Jwts.parser()
                    .verifyWith(secret)
                    .build()
                    .parseSignedClaims(jwt);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
