package com.trainersworkloadservice.service.util;

import com.trainersworkloadservice.model.MonthEntity;
import com.trainersworkloadservice.model.TrainerEntity;
import com.trainersworkloadservice.model.YearEntity;
import com.trainersworkloadservice.model.dto.DecreaseWorkloadParams;
import com.trainersworkloadservice.model.dto.IncreaseWorkloadParams;
import com.trainersworkloadservice.repository.TrainerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

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

        month.setHours(safeAddNonNegative(month.getHours(), params.hoursDelta()));

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

        long newHours = month.getHours() - params.hoursToRemove();

        if (newHours > 0) {
            month.setHours(newHours);
            repository.save(trainer);

            return;
        }

        year.removeMonth(month);

        if (year.getMonths().isEmpty()) {
            trainer.removeYear(year);
        }

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
                .orElseThrow(() -> new IllegalStateException("Trainer not found: " + username));
    }

    private YearEntity getYearOrThrow(TrainerEntity trainer, int workYear) {
        return trainer.getYears().stream()
                .filter(year -> year.getWorkYear() == workYear)
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Year not found: " + workYear));
    }

    private MonthEntity getMonthOrThrow(YearEntity year, int monthOfYear) {
        return year.getMonths().stream()
                .filter(month -> month.getMonthOfYear() == monthOfYear)
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Month not found: " + monthOfYear));
    }

    private long safeAddNonNegative(long base, long delta) {
        long nonNegDelta = Math.max(0L, delta);
        long result = Math.addExact(base, nonNegDelta);

        return Math.max(0L, result);
    }
}
