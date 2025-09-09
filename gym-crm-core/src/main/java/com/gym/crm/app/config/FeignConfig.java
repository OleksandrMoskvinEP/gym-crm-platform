package com.gym.crm.app.config;

import com.gym.crm.app.security.jwt.CrossServiceJwtTokenProvider;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class FeignConfig implements RequestInterceptor {
    private final CrossServiceJwtTokenProvider crossServiceJwtTokenProvider;

    @Override
    public void apply(RequestTemplate requestTemplate) {
        requestTemplate.header("Authorization", "Bearer " + crossServiceJwtTokenProvider.generateTokenForService());
    }
}
