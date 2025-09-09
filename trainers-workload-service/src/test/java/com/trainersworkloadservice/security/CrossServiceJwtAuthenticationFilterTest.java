package com.trainersworkloadservice.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.util.ReflectionTestUtils;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@ExtendWith(MockitoExtension.class)
class CrossServiceJwtAuthenticationFilterTest {
    public static final MockFilterChain FILTER_CHAIN = new MockFilterChain();
    private static final String SECRET = "TestSecretKeyThatIsLongEnoughForHS256Algo123";

    public static MockHttpServletRequest request;
    public static MockHttpServletResponse response;

    private CrossServiceJwtAuthenticationFilter filter;
    private SecretKey secretKey;

    @BeforeEach
    void setUp() {
        filter = new CrossServiceJwtAuthenticationFilter();
        secretKey = Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8));
        ReflectionTestUtils.setField(filter, "secretKey", secretKey);

        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
    }

    @Test
    void shouldAllowRequest_whenValidToken() throws Exception {
        String token = getValidToken();
        request.addHeader("Authorization", "Bearer " + token);

        filter.doFilter(request, response, FILTER_CHAIN);

        assertThat(response.getStatus()).isEqualTo(200);
    }

    @Test
    void shouldRejectRequest_whenInvalidSubject() throws Exception {
        String token = getInvalidToken();
        request.addHeader("Authorization", "Bearer " + token);

        filter.doFilter(request, response, FILTER_CHAIN);

        assertThat(response.getStatus()).isEqualTo(403);
        assertThat(response.getErrorMessage()).contains("Invalid service identity");
    }

    @Test
    void shouldRejectRequest_whenTokenIsInvalid() throws Exception {
        request.addHeader("Authorization", "Bearer invalid.token.value");

        filter.doFilter(request, response, FILTER_CHAIN);

        assertThat(response.getStatus()).isEqualTo(401);
        assertThat(response.getErrorMessage()).contains("Invalid JWT token");
    }

    private String getValidToken() {
        return Jwts.builder()
                .subject("core-service")
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + 60000))
                .signWith(secretKey)
                .compact();
    }

    private String getInvalidToken() {
        return Jwts.builder()
                .subject("another-service")
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + 60000))
                .signWith(secretKey)
                .compact();
    }

}