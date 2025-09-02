package com.trainersworkloadservice.service.util;

import com.trainersworkloadservice.model.MonthEntity;
import com.trainersworkloadservice.model.TrainerEntity;
import com.trainersworkloadservice.model.YearEntity;
import com.trainersworkloadservice.repository.TrainerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class WorkloadCalculateHelperTest {
    private TrainerEntity existingTrainer;

    @Mock
    private TrainerRepository trainerRepository;
    @InjectMocks
    private WorkloadCalculateHelper helper;

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
    void shouldCreateYearMonthAndAddsHours_whenNotExists() {
        ArgumentCaptor<TrainerEntity> captor = ArgumentCaptor.forClass(TrainerEntity.class);

        when(trainerRepository.findWithWorkloadByUsername("first_last")).thenReturn(Optional.of(existingTrainer));

        helper.addOrIncrementWorkload("first_last", "first", "last", true, (short) 2025, (short) 7, 3L);
        verify(trainerRepository).save(captor.capture());

        TrainerEntity saved = captor.getValue();
        YearEntity y = saved.getYears().stream().filter(v -> v.getWorkYear() == 2025).findFirst().orElse(null);
        assertThat(y).isNotNull();

        MonthEntity m = y.getMonths().stream().filter(v -> v.getMonthOfYear() == 7).findFirst().orElse(null);
        assertThat(m).isNotNull();
        assertThat(m.getHours()).isEqualTo(3L);
    }

    @Test
    void shouldIncrementHours_whenMonthExists() {
        YearEntity year = YearEntity.builder().workYear((short) 2025).build();
        MonthEntity month = MonthEntity.builder().monthOfYear((short) 7).hours(5L).build();
        year.addMonth(month);

        existingTrainer.addYear(year);

        when(trainerRepository.findWithWorkloadByUsername("first_last")).thenReturn(Optional.of(existingTrainer));

        helper.addOrIncrementWorkload("first_last", "first", "last", true, (short) 2025, (short) 7, 2L);

        verify(trainerRepository).save(existingTrainer);
        assertThat(month.getHours()).isEqualTo(7L);
    }

    @Test
    void shouldThrowsOnNegativeDelta() {
        assertThrows(IllegalArgumentException.class, () ->
                helper.addOrIncrementWorkload("first_last", "first", "last", true, (short) 2025, (short) 7, -2L)
        );
        verifyNoInteractions(trainerRepository);
    }

    @Test
    void shouldDoNothing_whenHoursZero() {
        YearEntity year = YearEntity.builder().workYear((short) 2025).build();
        MonthEntity month = MonthEntity.builder().monthOfYear((short) 7).hours(3L).build();
        year.addMonth(month);
        existingTrainer.addYear(year);

        when(trainerRepository.findWithWorkloadByUsername("username")).thenReturn(Optional.of(existingTrainer));

        helper.deleteOrDecrementWorkload("username", (short) 2025, (short) 7, 3L);

        verify(trainerRepository).save(existingTrainer);
        assertThat(year.getMonths()).isNotEmpty();
        assertThat(existingTrainer.getYears()).isNotEmpty();
    }


    @Test
    void shouldReturnHours_whenPresent() {
        YearEntity year = YearEntity.builder().workYear((short) 2025).build();
        MonthEntity month = MonthEntity.builder().monthOfYear((short) 7).hours(10L).build();
        year.addMonth(month);
        existingTrainer.addYear(year);

        when(trainerRepository.findWithWorkloadByUsername("username")).thenReturn(Optional.of(existingTrainer));

        Long actual = helper.getMonthlyWorkload("username", (short) 2025, (short) 7);

        assertThat(actual).isEqualTo(10L);
    }
}