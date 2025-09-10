package com.gym.crm.core.security.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@ActiveProfiles("test")
class CrossServiceJwtTokenProviderTest {
    @Value("${security.jwt.workloadServiceSecret}")
    private String secret;
    private SecretKey secretTestKey;

    @BeforeEach
    void setUp() {
        secretTestKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    @Autowired
    private CrossServiceJwtTokenProvider jwtTokenProvider;

    @Test
    void shouldGenerateTokenForService() {
        String actual = jwtTokenProvider.generateTokenForService();

        assertNotNull(actual);
        Claims claims = parseToken(actual);

        assertEquals("core-service", claims.getSubject());
        assertThat(claims.getExpiration()).isAfterOrEqualTo(claims.getIssuedAt());
    }

    private Claims parseToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(secretTestKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}