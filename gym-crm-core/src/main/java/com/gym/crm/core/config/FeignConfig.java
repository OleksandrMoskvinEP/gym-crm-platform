package com.gym.crm.core.config;

import com.gym.crm.core.security.jwt.CrossServiceJwtTokenProvider;
import feign.Logger;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import lombok.RequiredArgsConstructor;
import org.slf4j.MDC;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class FeignConfig implements RequestInterceptor {
    private final CrossServiceJwtTokenProvider crossServiceJwtTokenProvider;

    @Override
    public void apply(RequestTemplate requestTemplate) {
        requestTemplate.header("Authorization", "Bearer " + crossServiceJwtTokenProvider.generateTokenForService());

        String transactionId = MDC.get("transactionId");

        if (transactionId != null) {
            requestTemplate.header("X-Transaction-Id", transactionId);
        }
    }

    @Bean
    Logger.Level feignLoggerLevel() {
        return Logger.Level.FULL;
    }
}
