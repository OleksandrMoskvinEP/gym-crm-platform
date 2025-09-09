package com.trainersworkloadservice.config;

import com.trainersworkloadservice.security.CrossServiceJwtAuthenticationFilter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

import java.io.IOException;

@Configuration
public class TestSecurityConfig {
    @Bean
    public CrossServiceJwtAuthenticationFilter crossServiceJwtAuthenticationFilter() {
        return new CrossServiceJwtAuthenticationFilter() {
            @Override
            protected void doFilterInternal(HttpServletRequest request,
                                            HttpServletResponse response,
                                            FilterChain chain) throws IOException, ServletException {
                chain.doFilter(request, response);
            }
        };
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth.anyRequest().permitAll())
                .build();
    }
}
