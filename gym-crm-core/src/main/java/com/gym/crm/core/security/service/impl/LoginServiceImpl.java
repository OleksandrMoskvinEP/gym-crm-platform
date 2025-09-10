package com.gym.crm.core.security.service.impl;

import com.gym.crm.core.domain.model.RefreshToken;
import com.gym.crm.core.domain.model.User;
import com.gym.crm.core.exception.AuthorizationErrorException;
import com.gym.crm.core.mapper.UserMapper;
import com.gym.crm.core.repository.UserRepository;
import com.gym.crm.core.rest.JwtTokenResponse;
import com.gym.crm.core.rest.LoginRequest;
import com.gym.crm.core.security.service.AuthenticatedUserService;
import com.gym.crm.core.security.model.UserRole;
import com.gym.crm.core.security.jwt.JwtTokenProvider;
import com.gym.crm.core.security.model.AuthenticatedUser;
import com.gym.crm.core.security.service.RefreshTokenService;
import com.gym.crm.core.security.service.LoginService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class LoginServiceImpl implements LoginService {
    private static final Duration REFRESH_TTL = Duration.ofDays(14);

    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenService refreshTokenService;
    private final AuthenticatedUserService authenticatedUserService;
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    public JwtTokenResponse login(LoginRequest loginRequest) {
        AuthenticatedUser user = authenticatedUserService.getAuthenticatedUser(loginRequest);

        String accessToken = jwtTokenProvider.generateToken(user);

        RefreshToken refresh = jwtTokenProvider.generateRefreshToken(user.getUserId(), REFRESH_TTL);
        String rawRefresh = refreshTokenService.saveRefreshToken(refresh, user.getUserId());

        JwtTokenResponse response = new JwtTokenResponse();
        response.setAccessToken(accessToken);
        response.setRefreshToken(rawRefresh);

        return response;
    }

    @Override
    @Transactional
    public JwtTokenResponse refresh(String refreshToken) {
        Long userId = refreshTokenService.validateAndGetUserId(refreshToken)
                .orElseThrow(() -> new AuthorizationErrorException("Invalid or expired refresh token"));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AuthorizationErrorException("User not found: " + userId));

        UserRole role = authenticatedUserService.resolveUserRole(user.getUsername());

        AuthenticatedUser authUser = userMapper.toAuthenticatedUser(user).toBuilder()
                .role(role)
                .build();

        String newAccess = jwtTokenProvider.generateToken(authUser);
        RefreshToken newRefresh = jwtTokenProvider.generateRefreshToken(userId, REFRESH_TTL);
        String rawNewRefresh = refreshTokenService.saveRefreshToken(newRefresh, userId);

        refreshTokenService.delete(refreshToken);

        JwtTokenResponse response = new JwtTokenResponse();
        response.setAccessToken(newAccess);
        response.setRefreshToken(rawNewRefresh);

        return response;
    }

    @Override
    public void logout(String token) {
        refreshTokenService.delete(token);
        SecurityContextHolder.clearContext();
    }
}
