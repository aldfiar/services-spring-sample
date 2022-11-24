package ru.vernalis.servicesspringsample.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.Optional;

/**
 * Генерация и валидация JWT токена
 */
@Component
public class JwtService {
    private static final Logger LOGGER = LoggerFactory.getLogger(JwtService.class);
    private final Integer jwtTokenLifeTime;
    private final Key key;

    @Autowired
    public JwtService(@Value("${jwt.secret}") String secret, @Value("${jwt.lifetime}") Integer jwtTokenLifeTime) {
        this.key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret));
        this.jwtTokenLifeTime = jwtTokenLifeTime;
    }

    public String generateToken(String userName) {
        Instant currentTime = Instant.now();
        return Jwts.builder()
                .setIssuer("service-sample")
                .claim("name", userName)
                .setIssuedAt(Date.from(currentTime))
                .setExpiration(Date.from(currentTime.plus(Duration.ofSeconds(jwtTokenLifeTime))))
                .signWith(key, SignatureAlgorithm.HS512)
                .compact();
    }

    public Optional<String> getSender(String token) {
        Optional<String> result;
        try {
            JwtParser parser = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build();
            Jws<Claims> claimsJws = parser.parseClaimsJws(token);
            Claims body = claimsJws.getBody();
            result = Optional.of((String) body.get("name"));

        } catch (Exception e) {
            LOGGER.error("Invalid JWT: {0}", e);
            result = Optional.empty();
        }
        return result;
    }

    public boolean validateToken(String token) {
        try {
            JwtParser parser = Jwts.parserBuilder().setSigningKey(key).build();
            Jws<Claims> claimsJws = parser.parseClaimsJws(token);
            return true;
        } catch (io.jsonwebtoken.security.SecurityException | MalformedJwtException e) {
            LOGGER.error("Invalid JWT signature trace: {}", e.getMessage());
        } catch (ExpiredJwtException e) {
            LOGGER.error("Expired JWT token trace: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            LOGGER.error("Unsupported JWT token trace: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            LOGGER.error("JWT token compact of handler are invalid trace: {}", e.getMessage());
        }
        return false;

    }


}
