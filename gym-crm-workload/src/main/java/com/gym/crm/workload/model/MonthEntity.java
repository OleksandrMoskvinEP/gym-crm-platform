package com.gym.crm.workload.model;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Builder(toBuilder = true)
@NoArgsConstructor
@Data
@AllArgsConstructor
@EqualsAndHashCode
@ToString
public class MonthEntity {
    @NotNull
    @Min(1)
    @Max(12)
    private Integer monthOfYear;

    @NotNull
    private Long hours = 0L;
}
