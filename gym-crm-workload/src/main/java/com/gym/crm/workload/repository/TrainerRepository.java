package com.gym.crm.workload.repository;

import com.gym.crm.workload.model.TrainerEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface TrainerRepository extends MongoRepository<TrainerEntity, String> {
    Optional<TrainerEntity> findByUsername(String username);

    boolean existsByUsername(String username);

    Optional<TrainerEntity> findWithWorkloadByUsername(String username);

    void deleteByUsername(String username);
}
