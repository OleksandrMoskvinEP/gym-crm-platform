package com.gym.crm.core.repository;

import com.gym.crm.core.domain.model.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, String> {
    @Transactional
    void deleteByUserId(Long userId);
}
