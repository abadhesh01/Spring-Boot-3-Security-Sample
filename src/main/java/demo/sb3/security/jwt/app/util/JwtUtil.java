package demo.sb3.security.jwt.app.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.concurrent.TimeUnit;

@Component
public class JwtUtil {

    private final String key = "F5B1167DDD54367216CFA47BF3A26878FB13B11224EA4AE2E744BFB653";

    private final SecretKey secretKey = Keys.hmacShaKeyFor(key.getBytes(StandardCharsets.UTF_8));

    public String generateToken(String username) {
        return Jwts.builder()
                .subject(username)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(10)))
                .signWith(secretKey)
                .compact();
    }

    public Jws<Claims> extractClaimsJws(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token);
    }

    public Claims extractClaimsFromClaimsJws(String token) {
        return extractClaimsJws(token).getPayload();
    }

    public String extractUsername(String token) {
        return extractClaimsFromClaimsJws(token).getSubject();
    }

    public boolean isTokenNonExpired(String token) {
        Claims claimsFromToken = extractClaimsFromClaimsJws(token);
        return claimsFromToken.getExpiration().after(new Date(System.currentTimeMillis()));
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        return extractUsername(token).equals(userDetails.getUsername()) && isTokenNonExpired(token);
    }
}
