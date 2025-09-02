package com.trainersworkloadservice.repository;

import com.trainersworkloadservice.model.TrainerEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TrainerRepository extends JpaRepository<TrainerEntity, Long> {
    Optional<TrainerEntity> findByUsername(String username);

    boolean existsByUsername(String username);

    List<TrainerEntity> findAllByActiveTrue();
}
