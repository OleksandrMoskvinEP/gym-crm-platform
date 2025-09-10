package com.gym.crm.core.mapper;

import com.gym.crm.core.domain.dto.user.ChangeActivationStatusDto;
import com.gym.crm.core.domain.dto.user.UserUpdateRequest;
import com.gym.crm.core.domain.model.User;
import com.gym.crm.core.rest.ActivationStatusRequest;
import com.gym.crm.core.security.model.UserRole;
import com.gym.crm.core.security.model.AuthenticatedUser;
import com.gym.crm.core.security.model.UserCredentialsDto;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public interface UserMapper {
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    User update(@MappingTarget User user, UserUpdateRequest dto);

    @Mapping(source = "role", target = "role", qualifiedByName = "roleToString")
    UserCredentialsDto toCredentialsDto(AuthenticatedUser user);

    @Mapping(source = "id", target = "userId")
    @Mapping(target = "role", ignore = true)
    AuthenticatedUser toAuthenticatedUser(User user);

    @Named("roleToString")
    static String roleToString(UserRole role) {
        return role.name();
    }

    @Mapping(target = "username", source = "username")
    @Mapping(target = "isActive", source = "request.isActive")
    ChangeActivationStatusDto toChangeActivationStatusDto(String username, ActivationStatusRequest request);
}
