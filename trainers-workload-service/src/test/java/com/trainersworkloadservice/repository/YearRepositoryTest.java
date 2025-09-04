package com.trainersworkloadservice.repository;

import com.trainersworkloadservice.model.TrainerEntity;
import com.trainersworkloadservice.model.YearEntity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.LinkedHashSet;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
class YearRepositoryTest {
    @Autowired
    private YearRepository yearRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    void findByTrainerIdAndWorkYear_returnsEntity() {
        TrainerEntity trainer = persistTrainer("user1");
        addYearIntoTrainerEntity(trainer, 2024);

        entityManager.persistAndFlush(trainer);

        Optional<YearEntity> found = yearRepository.findByTrainerIdAndWorkYear(trainer.getId(), 2024);
        assertThat(found).isPresent();
        assertEquals(2024, found.get().getWorkYear());
        assertThat(found.get().getTrainer().getId()).isEqualTo(trainer.getId());
    }

    @Test
    void shouldChekIfExistsByTrainerIdAndWorkYear_andReturnTrueWhenExists() {
        TrainerEntity trainer = persistTrainer("user2");
        addYearIntoTrainerEntity(trainer, 2023);

        entityManager.persistAndFlush(trainer);

        boolean actual = yearRepository.existsByTrainerIdAndWorkYear(trainer.getId(), 2023);

        assertThat(actual).isTrue();
    }

    @Test
    void shouldChekIfExistsByTrainerIdAndWorkYear_andReturnFalseWhenDoesntExist() {
        TrainerEntity trainer = persistTrainer("user2");
        addYearIntoTrainerEntity(trainer, 2023);

        entityManager.persistAndFlush(trainer);

        boolean actual = yearRepository.existsByTrainerIdAndWorkYear(trainer.getId(), 2022);

        assertThat(actual).isFalse();
    }

    private static void addYearIntoTrainerEntity(TrainerEntity trainer, int yearValue) {
        YearEntity year = YearEntity.builder()
                .workYear(yearValue)
                .months(new LinkedHashSet<>())
                .build();
        trainer.addYear(year);
    }

    private TrainerEntity persistTrainer(String username) {
        TrainerEntity trainer = TrainerEntity.builder()
                .firstName("FirstName")
                .lastName("LastName")
                .username(username)
                .isActive(true)
                .years(new LinkedHashSet<>())
                .build();

        return entityManager.persistFlushFind(trainer);
    }
}