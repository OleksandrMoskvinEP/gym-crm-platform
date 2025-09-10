package com.gym.crm.workload.repository;

import com.gym.crm.workload.model.MonthEntity;
import com.gym.crm.workload.model.TrainerEntity;
import com.gym.crm.workload.model.YearEntity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.LinkedHashSet;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DataJpaTest
class MonthRepositoryTest {
    @Autowired
    private MonthRepository monthRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    void findByYearIdAndMonth_returnsMonth() {
        YearEntity year = prepareTrainerYearWithMonths();

        Optional<MonthEntity> found = monthRepository.findByYearIdAndMonthOfYear(year.getId(), 5);

        assertThat(found).isPresent();
        assertThat(found.get().getMonthOfYear()).isEqualTo(5);
        assertThat(found.get().getYear().getId()).isEqualTo(year.getId());
    }

    @Test
    void deleteByYearIdAndMonth_deletesCorrectly() {
        YearEntity year = prepareTrainerYearWithMonths();
        int deleted = monthRepository.deleteByYearIdAndMonthOfYear(year.getId(), 6);

        entityManager.flush();

        assertThat(deleted).isEqualTo(1);
        assertThat(monthRepository.findByYearIdAndMonthOfYear(year.getId(), 6)).isNotPresent();
        assertThat(monthRepository.findByYearIdAndMonthOfYear(year.getId(), 5)).isPresent();
    }

    private YearEntity prepareTrainerYearWithMonths() {
        TrainerEntity trainer = TrainerEntity.builder()
                .firstName("Name")
                .lastName("Surname")
                .username("with.months")
                .isActive(true)
                .years(new LinkedHashSet<>())
                .build();

        YearEntity year = YearEntity.builder()
                .workYear(2025)
                .months(new LinkedHashSet<>())
                .build();

        MonthEntity may = MonthEntity.builder()
                .monthOfYear(5)
                .hours(12L)
                .build();
        MonthEntity jun = MonthEntity.builder()
                .monthOfYear(6)
                .hours(8L)
                .build();

        year.addMonth(may);
        year.addMonth(jun);
        trainer.addYear(year);

        entityManager.persist(trainer);
        entityManager.persist(year);

        entityManager.flush();
        entityManager.clear();

        return entityManager.find(YearEntity.class, year.getId());
    }
}