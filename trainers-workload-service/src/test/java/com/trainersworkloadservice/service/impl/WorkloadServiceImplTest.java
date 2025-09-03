package com.trainersworkloadservice.service.impl;

import com.trainersworkloadservice.model.dto.ActionType;
import com.trainersworkloadservice.model.dto.MonthlyWorkloadRequest;
import com.trainersworkloadservice.model.dto.MonthlyWorkloadResponse;
import com.trainersworkloadservice.model.dto.WorkloadEventRequest;
import com.trainersworkloadservice.service.util.WorkloadCalculateHelper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class WorkloadServiceImplTest {
    @Mock
    private WorkloadCalculateHelper helper;
    @InjectMocks
    private WorkloadServiceImpl service;

    @Test
    void shouldCalculateAndStoreWorkload_routeAddToHelper() {
        WorkloadEventRequest request = new WorkloadEventRequest(
                "first_last", "first", "last", true,
                LocalDate.of(2025, 7, 15), 2L,
                ActionType.ADD
        );

        service.calculateAndStoreWorkload(request);

        verify(helper).addOrIncrementWorkload("first_last", "first", "last", true, (short) 2025, (short) 7, 2L);
    }

    @Test
    void shouldCalculateAndStoreWorkload_routeDeleteToHelper() {
        WorkloadEventRequest request = new WorkloadEventRequest(
                "first_last", "first", "last", true,
                LocalDate.of(2025, 7, 15), 5L,
                ActionType.DELETE
        );

        service.calculateAndStoreWorkload(request);

        verify(helper).deleteOrDecrementWorkload("first_last", (short) 2025, (short) 7, 5L);
    }

    @Test
    void shouldRetrieveAndGetMonthlyWorkload_MapHelperValueToResponse() {
        MonthlyWorkloadRequest request = new MonthlyWorkloadRequest("username", (short) 7, (short) 2025);

        when(helper.getMonthlyWorkload("username", (short) 2025, (short) 7)).thenReturn(5L);

        MonthlyWorkloadResponse actual = service.getMonthlyWorkload(request);

        assertThat(actual).isNotNull();
        assertThat(actual.username()).isEqualTo("username");
        assertThat(actual.year()).isEqualTo((short) 2025);
        assertThat(actual.month()).isEqualTo((short) 7);
        assertThat(actual.totalHours()).isEqualTo(5L);
        assertThat(actual.totalMinutes()).isEqualTo(300L);
    }
}