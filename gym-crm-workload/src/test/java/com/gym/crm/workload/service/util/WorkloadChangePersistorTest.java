package com.gym.crm.workload.service.util;

import com.gym.crm.workload.model.MonthEntity;
import com.gym.crm.workload.model.TrainerEntity;
import com.gym.crm.workload.model.YearEntity;
import com.gym.crm.workload.model.dto.DecreaseWorkloadParams;
import com.gym.crm.workload.model.dto.IncreaseWorkloadParams;
import com.gym.crm.workload.repository.TrainerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class WorkloadChangePersistorTest {
    public static final YearEntity YEAR_ENTITY = YearEntity.builder().workYear(2025).build();
    public static final MonthEntity MONTH_ENTITY = MonthEntity.builder().monthOfYear(7).hours(5L).build();

    private TrainerEntity existingTrainer;

    @Mock
    private TrainerRepository trainerRepository;
    @InjectMocks
    private WorkloadChangePersistor helper;

    @BeforeEach
    void setUp() {
        existingTrainer = TrainerEntity.builder()
                .username("first_last")
                .firstName("first")
                .lastName("last")
                .isActive(true)
                .build();
    }

    @Test
    void shouldCreateTrainerYearMonthAndAddsHours_whenNotExists() {
        var captor = ArgumentCaptor.forClass(TrainerEntity.class);
        TrainerEntity expectedTrainer = buildTrainerWithYearAndMonth();

        when(trainerRepository.findWithWorkloadByUsername("first_last"))
                .thenReturn(Optional.of(existingTrainer));

        helper.increaseWorkload(getIncreaseParams());

        verify(trainerRepository).save(captor.capture());
        TrainerEntity saved = captor.getValue();

        assertThat(saved).isEqualTo(expectedTrainer);
    }

    @Test
    void shouldIncrementHours_whenMonthExists() {
        YEAR_ENTITY.addMonth(MONTH_ENTITY);
        existingTrainer.addYear(YEAR_ENTITY);

        when(trainerRepository.findWithWorkloadByUsername("first_last")).thenReturn(Optional.of(existingTrainer));

        helper.increaseWorkload(getIncreaseParams());

        verify(trainerRepository).save(existingTrainer);
        assertThat(MONTH_ENTITY.getHours()).isEqualTo(5L);
    }

    @Test
    void shouldThrowsOnNegativeDelta() {
        assertThrows(IllegalArgumentException.class,
                () -> helper.increaseWorkload(getWrongIncreaseParams()));

        verifyNoInteractions(trainerRepository);
    }

    @Test
    void shouldDoNothing_whenHoursZero() {
        YEAR_ENTITY.addMonth(MONTH_ENTITY);
        existingTrainer.addYear(YEAR_ENTITY);

        helper.decreaseWorkload(getDecreaseParamsWithZeroHours());

        verifyNoInteractions(trainerRepository);
        assertThat(YEAR_ENTITY.getMonths()).isNotEmpty();
        assertThat(existingTrainer.getYears()).isNotEmpty();
    }

    @Test
    void shouldReturnHours_whenPresent() {
        YEAR_ENTITY.addMonth(MONTH_ENTITY);
        existingTrainer.addYear(YEAR_ENTITY);

        when(trainerRepository.findWithWorkloadByUsername("username")).thenReturn(Optional.of(existingTrainer));

        Long actual = helper.getMonthlyWorkload("username", 2025, 7);

        assertThat(actual).isEqualTo(5L);
    }

    private IncreaseWorkloadParams getIncreaseParams() {
        return new IncreaseWorkloadParams("first_last",
                "first",
                "last",
                true,
                2025,
                7,
                2L
        );
    }

    private IncreaseWorkloadParams getWrongIncreaseParams() {
        return new IncreaseWorkloadParams("first_last",
                "first",
                "last",
                true,
                2025,
                7,
                -2L
        );
    }

    private DecreaseWorkloadParams getDecreaseParamsWithZeroHours() {
        return new DecreaseWorkloadParams("first_last",
                2025,
                7,
                0L
        );
    }

    private TrainerEntity buildTrainerWithYearAndMonth() {
        MonthEntity month = MonthEntity.builder()
                .monthOfYear(7)
                .hours(5L)
                .build();

        YearEntity year = YearEntity.builder()
                .workYear(2025)
                .months(new LinkedHashSet<>(Set.of(month)))
                .build();

        return TrainerEntity.builder()
                .username("first_last")
                .firstName("first")
                .lastName("last")
                .isActive(true)
                .years(new LinkedHashSet<>(Set.of(year)))
                .build();
    }
}