package com.gym.crm.core.security.service;

import com.gym.crm.core.rest.LoginRequest;
import com.gym.crm.core.security.model.AuthenticatedUser;
import com.gym.crm.core.security.model.UserRole;

public interface AuthenticatedUserService {
    AuthenticatedUser getAuthenticatedUser(LoginRequest loginRequest);

    UserRole resolveUserRole(String username);
}
