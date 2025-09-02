package com.trainersworkloadservice.service.util;

import com.trainersworkloadservice.model.MonthEntity;
import com.trainersworkloadservice.model.TrainerEntity;
import com.trainersworkloadservice.model.YearEntity;
import com.trainersworkloadservice.repository.TrainerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class WorkloadCalculateHelper {
    private final TrainerRepository trainerRepo;

    public void addOrIncrementWorkload(String username,
                                       String firstName,
                                       String lastName,
                                       boolean active,
                                       short workYear,
                                       short monthOfYear,
                                       long hoursDelta) {
        if (hoursDelta == 0) return;
        if (hoursDelta < 0) throw new IllegalArgumentException("hoursDelta must be > 0");

        TrainerEntity trainer = getOrCreateTrainer(username, firstName, lastName, active);
        YearEntity year = getOrCreateYear(trainer, workYear);
        MonthEntity month = getOrCreateMonth(year, monthOfYear);

        month.setHours(safeAddNonNegative(month.getHours(), hoursDelta));

        trainerRepo.save(trainer);
    }

    public void deleteOrDecrementWorkload(String username,
                                          short workYear,
                                          short monthOfYear,
                                          long hoursToRemove) {
        if (hoursToRemove == 0) return;
        if (hoursToRemove < 0) throw new IllegalArgumentException("hoursToRemove must be > 0");

        TrainerEntity trainer = getTrainerOrThrow(username);
        YearEntity year = getYearOrThrow(trainer, workYear);
        MonthEntity month = getMonthOrThrow(year, monthOfYear);

        long newHours = month.getHours() - hoursToRemove;
        if (newHours > 0) {
            month.setHours(newHours);
        } else {
            year.removeMonth(month);

            if (year.getMonths().isEmpty()) {
                trainer.removeYear(year);
            }
        }
        trainerRepo.save(trainer);
    }

    public Long getMonthlyWorkload(String username, short workYear, short monthOfYear) {
        TrainerEntity trainer = getTrainerOrThrow(username);
        YearEntity year = getYearOrThrow(trainer, workYear);
        MonthEntity month = getMonthOrThrow(year, monthOfYear);

        return month.getHours();
    }

    private TrainerEntity getOrCreateTrainer(String username,
                                             String firstName,
                                             String lastName,
                                             boolean active) {
        return trainerRepo.findWithWorkloadByUsername(username)
                .orElseGet(() -> TrainerEntity.builder()
                        .username(username)
                        .firstName(firstName)
                        .lastName(lastName)
                        .isActive(active)
                        .build());
    }

    private YearEntity getOrCreateYear(TrainerEntity trainer, short workYear) {
        return trainer.getYears().stream()
                .filter(year -> year.getWorkYear() == workYear)
                .findFirst()
                .orElseGet(() -> {
                    YearEntity yearEntity = YearEntity.builder()
                            .workYear(workYear)
                            .build();
                    trainer.addYear(yearEntity);

                    return yearEntity;
                });
    }

    private MonthEntity getOrCreateMonth(YearEntity year, short monthOfYear) {
        return year.getMonths().stream()
                .filter(month -> month.getMonthOfYear() == monthOfYear)
                .findFirst()
                .orElseGet(() -> {
                    MonthEntity monthEntity = MonthEntity.builder()
                            .monthOfYear(monthOfYear)
                            .hours(0L)
                            .build();
                    year.addMonth(monthEntity);

                    return monthEntity;
                });
    }

    private TrainerEntity getTrainerOrThrow(String username) {
        return trainerRepo.findWithWorkloadByUsername(username)
                .orElseThrow(() -> new IllegalStateException("Trainer not found: " + username));
    }

    private YearEntity getYearOrThrow(TrainerEntity trainer, short workYear) {
        return trainer.getYears().stream()
                .filter(year -> year.getWorkYear() == workYear)
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Year not found: " + workYear));
    }

    private MonthEntity getMonthOrThrow(YearEntity year, short monthOfYear) {
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
