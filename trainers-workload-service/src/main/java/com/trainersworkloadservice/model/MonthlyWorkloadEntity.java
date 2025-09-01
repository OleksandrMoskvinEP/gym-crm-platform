package com.trainersworkloadservice.model;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "trainers_monthly_workloads")
@Builder(toBuilder = true)
@NoArgsConstructor
@Data
@AllArgsConstructor
@EqualsAndHashCode
public class MonthlyWorkloadEntity {
    @EmbeddedId
    private MonthlyWorkloadId monthlyWorkloadId;

    @MapsId("trainerId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "trainer_id", nullable = false)
    private com.trainersworkloadservice.model.TrainerEntity trainer;

    @Column(name = "hours", nullable = false)
    private Long hours;
}
