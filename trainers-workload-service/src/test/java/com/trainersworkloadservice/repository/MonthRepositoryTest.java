package com.trainersworkloadservice.repository;

import com.trainersworkloadservice.model.MonthEntity;
import com.trainersworkloadservice.model.TrainerEntity;
import com.trainersworkloadservice.model.YearEntity;
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

        Optional<MonthEntity> found = monthRepository.findByYearIdAndMonthOfYear(year.getId(), (short) 5);

        assertThat(found).isPresent();
        assertThat(found.get().getMonthOfYear()).isEqualTo((short) 5);
        assertThat(found.get().getYear().getId()).isEqualTo(year.getId());
    }

    @Test
    void deleteByYearIdAndMonth_deletesCorrectly() {
        YearEntity year = prepareTrainerYearWithMonths();
        int deleted = monthRepository.deleteByYearIdAndMonthOfYear(year.getId(), (short) 6);

        entityManager.flush();

        assertThat(deleted).isEqualTo(1);
        assertThat(monthRepository.findByYearIdAndMonthOfYear(year.getId(), (short) 6)).isNotPresent();
        assertThat(monthRepository.findByYearIdAndMonthOfYear(year.getId(), (short) 5)).isPresent();
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
                .workYear((short) 2025)
                .months(new LinkedHashSet<>())
                .build();

        MonthEntity may = MonthEntity.builder()
                .monthOfYear((short) 5)
                .hours(12L)
                .build();
        MonthEntity jun = MonthEntity.builder()
                .monthOfYear((short) 6)
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