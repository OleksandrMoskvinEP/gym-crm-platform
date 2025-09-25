package com.gym.crm.workload.repository;

import com.gym.crm.workload.model.MonthEntity;
import com.gym.crm.workload.model.TrainerEntity;
import com.gym.crm.workload.model.YearEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.util.LinkedHashSet;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class TrainerRepositoryTest extends MongoTestContainer {
    @Autowired
    private MongoTemplate mongoTemplate;
    @Autowired
    private TrainerRepository trainerRepository;

    @BeforeEach
    void cleanDb() {
        trainerRepository.deleteAll();
    }

    @Test
    void shouldFindTrainerByUsername() {
        TrainerEntity expected = persistTrainerWithWorkload("arnold_schwarzenegger");

        Optional<TrainerEntity> actual = trainerRepository.findByUsername("arnold_schwarzenegger");

        assertThat(actual).isPresent();
        assertThat(actual.get().getId()).isEqualTo(expected.getId());
        assertThat(actual.get().getUsername()).isEqualTo("arnold_schwarzenegger");
    }

    @Test
    void shouldCheckIfExistsByUsername_whenExists_returnsTrue() {
        persistTrainerWithWorkload("active.user");

        boolean actual = trainerRepository.existsByUsername("active.user");

        assertThat(actual).isTrue();
    }

    @Test
    void shouldCheckIfExistsByUsername_whenDoesntExist_returnsFalse() {
        persistTrainerWithWorkload("active.user");

        boolean actual = trainerRepository.existsByUsername("someone-else");

        assertThat(actual).isFalse();
    }

    @Test
    @DisplayName("findWithWorkloadByUsername fetches years and months via entity graph")
    void shouldFindWithWorkloadByUsername_fetchesWorkload() {
        persistTrainerWithWorkload("graph.user");

        Optional<TrainerEntity> actual = trainerRepository.findWithWorkloadByUsername("graph.user");

        assertThat(actual).isPresent();

        TrainerEntity trainer = actual.get();
        assertThat(trainer.getYears()).isNotNull();
        assertThat(trainer.getYears()).hasSize(1);

        YearEntity year = trainer.getYears().iterator().next();
        assertThat(year.getMonths()).isNotNull();
        assertThat(year.getMonths()).hasSize(2);
    }

    @Test
    void shouldDeleteTrainerByUsername() {
        persistTrainerWithWorkload("to.delete");

        trainerRepository.deleteByUsername("to.delete");

        assertThat(trainerRepository.findByUsername("to.delete")).isNotPresent();
    }

    private TrainerEntity persistTrainerWithWorkload(String username) {
        TrainerEntity trainer = TrainerEntity.builder()
                .firstName("Arnold")
                .lastName("Schwarzenegger")
                .username(username)
                .isActive(true)
                .years(new LinkedHashSet<>())
                .build();

        YearEntity year = YearEntity.builder()
                .workYear(2025)
                .months(new LinkedHashSet<>())
                .build();

        MonthEntity jan = MonthEntity.builder()
                .monthOfYear(1)
                .hours(10L)
                .build();
        MonthEntity feb = MonthEntity.builder()
                .monthOfYear(2)
                .hours(15L)
                .build();

        year.addMonth(jan);
        year.addMonth(feb);
        trainer.addYear(year);

        return mongoTemplate.save(trainer);
    }
}