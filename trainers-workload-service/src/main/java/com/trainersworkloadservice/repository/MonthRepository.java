package com.trainersworkloadservice.repository;

import com.trainersworkloadservice.model.MonthEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MonthRepository extends JpaRepository<MonthEntity,Long> {
    Optional<MonthEntity> findByYearIdAndMonth(Long yearId, short month);

    int deleteByYearIdAndMonth(Long yearId, short month);
}
