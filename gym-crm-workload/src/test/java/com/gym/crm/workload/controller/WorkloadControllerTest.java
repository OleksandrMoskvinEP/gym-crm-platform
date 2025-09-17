package com.gym.crm.workload.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gym.crm.workload.config.TestSecurityConfig;
import com.gym.crm.workload.model.dto.MonthlyWorkloadRequest;
import com.gym.crm.workload.model.dto.MonthlyWorkloadResponse;
import com.gym.crm.workload.service.WorkloadService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = WorkloadController.class)
@Import(TestSecurityConfig.class)
class WorkloadControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockitoBean
    private WorkloadService workloadService;

    @Test
    @DisplayName("GET monthly workload returns 200 with body")
    void getMonthlyWorkload_success() throws Exception {
        MonthlyWorkloadResponse response = new MonthlyWorkloadResponse(
                "arnold_schwarzenegger",
                2025,
                8,
                10L,
                30L
        );

        when(workloadService.getMonthlyWorkload(any(MonthlyWorkloadRequest.class)))
                .thenReturn(response);

        mockMvc.perform(get("/api/v1/trainers-workload/{username}/{year}/{month}", "arnold_schwarzenegger", 2025, 8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.username").value("arnold_schwarzenegger"))
                .andExpect(jsonPath("$.year").value(2025))
                .andExpect(jsonPath("$.month").value(8))
                .andExpect(jsonPath("$.totalHours").value(10))
                .andExpect(jsonPath("$.totalMinutes").value(30));
    }
}