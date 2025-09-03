package com.trainersworkloadservice.repository;

import com.trainersworkloadservice.model.YearEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface YearRepository extends JpaRepository<YearEntity, Long> {
    Optional<YearEntity> findByTrainerIdAndWorkYear(Long trainerId, int workYear);

    boolean existsByTrainerIdAndWorkYear(Long trainerId, int workYear);
}
