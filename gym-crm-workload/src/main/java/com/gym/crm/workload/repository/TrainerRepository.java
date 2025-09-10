package com.gym.crm.workload.repository;

import com.gym.crm.workload.model.TrainerEntity;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TrainerRepository extends JpaRepository<TrainerEntity, Long> {
    Optional<TrainerEntity> findByUsername(String username);

    boolean existsByUsername(String username);

    @EntityGraph(attributePaths = {"years", "years.months"})
    Optional<TrainerEntity> findWithWorkloadByUsername(String username);

    void deleteByUsername(String username);
}
