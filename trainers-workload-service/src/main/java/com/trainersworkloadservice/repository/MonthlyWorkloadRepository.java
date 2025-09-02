package com.trainersworkloadservice.repository;

import com.trainersworkloadservice.model.MonthlyWorkloadEntity;
import com.trainersworkloadservice.model.MonthlyWorkloadId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface MonthlyWorkloadRepository extends JpaRepository<MonthlyWorkloadEntity, MonthlyWorkloadId> {
    Optional<MonthlyWorkloadEntity> findByMonthlyWorkloadId_TrainerIdAndMonthlyWorkloadId_YearAndMonthlyWorkloadId_Month(
            Long trainerId, short year, short month);

    @Query("""
            select coalesce(sum(w.hours), 0)
              from MonthlyWorkloadEntity w
             where w.monthlyWorkloadId.trainerId = :trainerId
               and w.monthlyWorkloadId.year      = :year
               and w.monthlyWorkloadId.month     = :month
            """)
    long sumMonthHours(Long trainerId, short year, short month);

    @Modifying
    @Query("""
           update MonthlyWorkloadEntity w
              set w.hours = w.hours + :delta
            where w.monthlyWorkloadId.trainerId = :trainerId
              and w.monthlyWorkloadId.year      = :year
              and w.monthlyWorkloadId.month     = :month
           """)
    int incrementHours(long delta, Long trainerId, short year, short month);
}
