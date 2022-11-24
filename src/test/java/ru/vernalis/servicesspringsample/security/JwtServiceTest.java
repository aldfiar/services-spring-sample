package ru.vernalis.servicesspringsample.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.security.Key;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class JwtServiceTest {
    private JwtService jwtService;
    private final int duration = 10;
    private final String secret = "rYy0NEEbXK5nrTd13FEUjwGVpWAVWB9YvNqrcqfTVGEllPVlxzbgshSXOobTmqqdjna0goNDSjtgHw4kMvDgFeLpFp+2igJgsV0JRererJT+5mfSo5mFVyVIg2Ne2z20NMsVI7zvXs8s/mtope7H3E5LMU0m8cx2PMxDeGldQrXIpNsB7+guHLIthDZbcdlgyTcahADeHPilYOCsTTjQFDdO96XKxuUCjlJTKg==% ";

    @BeforeEach
    void setUp() {
        this.jwtService = new JwtService(secret , duration);
    }


    @Test
    public void testGenerateToken(){
        String lar = this.jwtService.generateToken("lar");
        assertNotNull(lar);
    }

    @Test
    public void testValidateToken(){
        String validToken = createValidToken();
        boolean b = this.jwtService.validateToken(validToken);
        assertTrue(b, "Token should be valid");
    }

    @Test
    public void testInvalidToken(){
        String validToken = createInvalidToken();
        boolean b = this.jwtService.validateToken(validToken);
        assertFalse(b, "Token should be invalid");
    }

    @Test
    public void testGetClaim(){
        String validToken = createValidToken();
        Optional<String> sender = this.jwtService.getSender(validToken);
        assertEquals("testUser", sender.get());
    }

    private String createInvalidToken() {
        Key otherKey = Keys.hmacShaKeyFor(Decoders.BASE64
                .decode(secret));
        Instant currentTime = Instant.now();
        return Jwts.builder()
                .setIssuer("service-sample")
                .claim("name", "testUser")
                .signWith(otherKey, SignatureAlgorithm.HS512)
                .setExpiration(Date.from(currentTime.minus(Duration.ofSeconds(duration))))
                .compact();
    }

    private String createValidToken() {
        Key otherKey = Keys.hmacShaKeyFor(Decoders.BASE64
                .decode(secret));
        Instant currentTime = Instant.now();
        return Jwts.builder()
                .setIssuer("service-sample")
                .claim("name", "testUser")
                .signWith(otherKey, SignatureAlgorithm.HS512)
                .setExpiration(Date.from(currentTime.plus(Duration.ofSeconds(duration))))
                .compact();
    }
}