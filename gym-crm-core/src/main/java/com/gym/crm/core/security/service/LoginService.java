package com.gym.crm.core.security.service;

import com.gym.crm.core.rest.JwtTokenResponse;
import com.gym.crm.core.rest.LoginRequest;

public interface LoginService {
    JwtTokenResponse login(LoginRequest loginRequest);

    JwtTokenResponse refresh(String refreshToken);

    void logout(String token);
}
