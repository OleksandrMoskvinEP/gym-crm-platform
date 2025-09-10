package com.gym.crm.workload.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.LinkedHashSet;
import java.util.Set;

import static jakarta.persistence.FetchType.LAZY;

@Entity
@Table(name = "years", uniqueConstraints = @UniqueConstraint(columnNames = {"trainer_id", "work_year"}))
@Builder(toBuilder = true)
@NoArgsConstructor
@Data
@AllArgsConstructor
@EqualsAndHashCode(exclude = {"trainer", "months"})
@ToString(exclude = {"trainer", "months"})
public class YearEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(nullable = false)
    private Integer workYear;

    @Builder.Default
    @OneToMany(mappedBy = "year", cascade = CascadeType.ALL, orphanRemoval = true, fetch = LAZY)
    @OrderBy("monthOfYear ASC")
    private Set<MonthEntity> months = new LinkedHashSet<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "trainer_id", nullable = false)
    private TrainerEntity trainer;

    public void addMonth(MonthEntity m) {
        months.add(m);
        m.setYear(this);
    }

    public void removeMonth(MonthEntity m) {
        months.remove(m);
        m.setYear(null);
    }
}
