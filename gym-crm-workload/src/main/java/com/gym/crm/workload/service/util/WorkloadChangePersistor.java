package com.gym.crm.workload.service.util;

import com.gym.crm.workload.model.MonthEntity;
import com.gym.crm.workload.model.TrainerEntity;
import com.gym.crm.workload.model.YearEntity;
import com.gym.crm.workload.model.dto.DecreaseWorkloadParams;
import com.gym.crm.workload.model.dto.IncreaseWorkloadParams;
import com.gym.crm.workload.repository.TrainerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class WorkloadChangePersistor {
    private final TrainerRepository repository;

    public void increaseWorkload(IncreaseWorkloadParams params) {
        if (params.hoursDelta() == 0) {
            return;
        }
        if (params.hoursDelta() < 0) {
            throw new IllegalArgumentException("training duration must be > 0");
        }

        TrainerEntity trainer = getOrCreateTrainer(params.username(), params.firstName(), params.lastName(), params.active());
        YearEntity year = getOrCreateYear(trainer, params.workYear());
        MonthEntity month = getOrCreateMonth(year, params.monthOfYear());

        month.setHours(safeAddNonNegative(month.getHours(), params.hoursDelta() / 60));

        log.info("Increasing workload of trainer {} by {} hours in {}/{}",
                trainer.getUsername(), params.hoursDelta() / 60, params.monthOfYear(), params.workYear());
        repository.save(trainer);
    }

    public void decreaseWorkload(DecreaseWorkloadParams params) {
        if (params.hoursToRemove() == 0) {
            return;
        }

        if (params.hoursToRemove() < 0) {
            throw new IllegalArgumentException("hoursToRemove must be > 0");
        }

        TrainerEntity trainer = getTrainerOrThrow(params.username());
        YearEntity year = getYearOrThrow(trainer, params.workYear());
        MonthEntity month = getMonthOrThrow(year, params.monthOfYear());

        long newHours = month.getHours() - params.hoursToRemove() / 60;

        if (newHours > 0) {
            month.setHours(newHours);

            log.info("Decreasing workload of trainer {} by {} hours in {}/{}",
                    trainer.getUsername(), params.hoursToRemove() / 60, params.monthOfYear(), params.workYear());
            repository.save(trainer);

            return;
        }

        year.removeMonth(month);

        if (year.getMonths().isEmpty()) {
            trainer.removeYear(year);
        }

        log.info("Decreasing workload of trainer {} by {} hours in {}/{}. Month removed.",
                trainer.getUsername(), params.hoursToRemove() / 60, params.monthOfYear(), params.workYear());
        repository.save(trainer);
    }

    public Long getMonthlyWorkload(String username, int workYear, int monthOfYear) {
        TrainerEntity trainer = getTrainerOrThrow(username);
        YearEntity year = getYearOrThrow(trainer, workYear);
        MonthEntity month = getMonthOrThrow(year, monthOfYear);

        return month.getHours();
    }

    private TrainerEntity getOrCreateTrainer(String username,
                                             String firstName,
                                             String lastName,
                                             boolean active) {
        return repository.findWithWorkloadByUsername(username)
                .orElseGet(() -> TrainerEntity.builder()
                        .username(username)
                        .firstName(firstName)
                        .lastName(lastName)
                        .isActive(active)
                        .build());
    }

    private YearEntity getOrCreateYear(TrainerEntity trainer, int workYear) {
        return trainer.getYears().stream()
                .filter(year -> year.getWorkYear() == workYear)
                .findFirst()
                .orElseGet(() -> buildYearEntity(trainer, workYear));
    }

    private static YearEntity buildYearEntity(TrainerEntity trainer, int workYear) {
        YearEntity yearEntity = YearEntity.builder()
                .workYear(workYear)
                .build();
        trainer.addYear(yearEntity);

        return yearEntity;
    }

    private MonthEntity getOrCreateMonth(YearEntity year, int monthOfYear) {
        return year.getMonths().stream()
                .filter(month -> month.getMonthOfYear() == monthOfYear)
                .findFirst()
                .orElseGet(() -> buildMonthEntity(year, monthOfYear));
    }

    private static MonthEntity buildMonthEntity(YearEntity year, int monthOfYear) {
        MonthEntity monthEntity = MonthEntity.builder()
                .monthOfYear(monthOfYear)
                .hours(0L)
                .build();
        year.addMonth(monthEntity);

        return monthEntity;
    }

    private TrainerEntity getTrainerOrThrow(String username) {
        return repository.findWithWorkloadByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("Trainer not found: " + username));
    }

    private YearEntity getYearOrThrow(TrainerEntity trainer, int workYear) {
        return trainer.getYears().stream()
                .filter(year -> year.getWorkYear() == workYear)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Year not found: " + workYear));
    }

    private MonthEntity getMonthOrThrow(YearEntity year, int monthOfYear) {
        return year.getMonths().stream()
                .filter(month -> month.getMonthOfYear() == monthOfYear)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Month not found: " + monthOfYear));
    }

    private long safeAddNonNegative(long base, long delta) {
        long nonNegDelta = Math.max(0L, delta);
        long result = Math.addExact(base, nonNegDelta);

        return Math.max(0L, result);
    }
}
