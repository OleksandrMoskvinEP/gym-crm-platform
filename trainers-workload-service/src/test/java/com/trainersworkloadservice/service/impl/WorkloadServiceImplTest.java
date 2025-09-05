package com.trainersworkloadservice.service.impl;

import com.trainersworkloadservice.model.dto.ActionType;
import com.trainersworkloadservice.model.dto.DecreaseWorkloadParams;
import com.trainersworkloadservice.model.dto.IncreaseWorkloadParams;
import com.trainersworkloadservice.model.dto.MonthlyWorkloadRequest;
import com.trainersworkloadservice.model.dto.MonthlyWorkloadResponse;
import com.trainersworkloadservice.model.dto.WorkloadEventRequest;
import com.trainersworkloadservice.service.util.WorkloadChangePersistor;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class WorkloadServiceImplTest {
    @Mock
    private WorkloadChangePersistor helper;
    @InjectMocks
    private WorkloadServiceImpl service;

    @Test
    void shouldCalculateAndStoreWorkload_routeAddToHelper() {
        WorkloadEventRequest request = getAddEventRequest();

        service.calculateAndStoreWorkload(request);

        verify(helper).increaseWorkload(getIncreaseParams());
    }

    @Test
    void shouldCalculateAndStoreWorkload_routeDeleteToHelper() {
        WorkloadEventRequest request = getDeleteEventRequest();

        service.calculateAndStoreWorkload(request);

        verify(helper).decreaseWorkload(getDecreaseParams());
    }

    @Test
    void shouldRetrieveAndGetMonthlyWorkload_MapHelperValueToResponse() {
        MonthlyWorkloadRequest request = getMonthlyWorkloadRequest();

        when(helper.getMonthlyWorkload("username", 2025, 7)).thenReturn(5L);

        MonthlyWorkloadResponse actual = service.getMonthlyWorkload(request);

        assertThat(actual).isNotNull();
        assertThat(actual.username()).isEqualTo("username");
        assertEquals(2025, actual.year());
        assertEquals(7, actual.month());
        assertThat(actual.totalHours()).isEqualTo(5L);
        assertThat(actual.totalMinutes()).isEqualTo(300L);
    }

    private IncreaseWorkloadParams getIncreaseParams() {
        return new IncreaseWorkloadParams("first_last",
                "first",
                "last",
                true,
                2025,
                7,
                2L
        );
    }

    private DecreaseWorkloadParams getDecreaseParams() {
        return new DecreaseWorkloadParams("first_last",
                2025,
                7,
                5L
        );
    }

    private static WorkloadEventRequest getAddEventRequest() {
        return new WorkloadEventRequest(
                "first_last", "first", "last", true,
                LocalDate.of(2025, 7, 15), 2,
                ActionType.ADD.name()
        );
    }

    private static WorkloadEventRequest getDeleteEventRequest() {
        return new WorkloadEventRequest(
                "first_last", "first", "last", true,
                LocalDate.of(2025, 7, 15), 5,
                ActionType.DELETE.name()
        );
    }

    private static MonthlyWorkloadRequest getMonthlyWorkloadRequest() {
        return new MonthlyWorkloadRequest("username", 2025, 7);
    }
}