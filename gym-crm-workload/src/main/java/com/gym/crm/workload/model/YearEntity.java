package com.gym.crm.workload.model;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.LinkedHashSet;
import java.util.Set;

@Builder(toBuilder = true)
@NoArgsConstructor
@Data
@AllArgsConstructor
@EqualsAndHashCode(exclude = {"months"})
@ToString(exclude = {"months"})
public class YearEntity {
    @Field("workYear")
    @NotNull
    private Integer workYear;

    @Builder.Default
    private Set<MonthEntity> months = new LinkedHashSet<>();

    public void addMonth(MonthEntity m) {
        months.add(m);
    }

    public void removeMonth(MonthEntity m) {
        months.remove(m);
    }
}
