package com.trainersworkloadservice.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@Builder(toBuilder = true)
@Data
@AllArgsConstructor
@EqualsAndHashCode
@Getter
@NoArgsConstructor
public class MonthlyWorkloadId {
    @Column(name = "trainer_id", nullable = false)
    private Long trainerId;
    @Column(name = "work_year", nullable = false)
    private Short year;
    @Column(name = "work_month", nullable = false)
    private Short month;
}
