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
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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

    @Test
    @DisplayName("POST update workload returns 200")
    void updateMonthlyWorkload_success() throws Exception {
        String requestBody = getRequestBody();

        mockMvc.perform(post("/api/v1/trainers-workload")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("POST with invalid body returns 400 and error message")
    void updateMonthlyWorkload_validationError() throws Exception {
        String invalidRequestBody = getInvalidRequestBody();

        mockMvc.perform(post("/api/v1/trainers-workload")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidRequestBody))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.code").value("BAD_REQUEST"));
    }

    @Test
    @DisplayName("POST service throws IllegalArgumentException -> 400")
    void updateMonthlyWorkload_illegalArgument() throws Exception {
        String requestBody = getIncorrectRequestBody();

        doThrow(new IllegalArgumentException("Bad input"))
                .when(workloadService).calculateAndStoreWorkload(any());

        mockMvc.perform(post("/api/v1/trainers-workload")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("BAD_REQUEST"));
    }

    private static String getIncorrectRequestBody() {
        return """
                {
                  "username": "john.doe",
                  "firstName": "John",
                  "lastName": "Doe",
                  "isActive": true,
                  "trainingDate": "2025-08-12",
                  "trainingDuration": 90,
                  "actionType": "INCREASE"
                }
                """;
    }

    private static String getInvalidRequestBody() {
        return """
                {
                  "username": "",
                  "firstName": "",
                  "lastName": "",
                  "isActive": null,
                  "trainingDate": null,
                  "trainingDuration": 0,
                  "actionType": ""
                }
                """;
    }

    private static String getRequestBody() {
        return """
                {
                  "username": "arnold_schwarzenegger",
                  "firstName": "Arnold",
                  "lastName": "Schwarzenegger",
                  "isActive": true,
                  "trainingDate": "2025-08-12",
                  "trainingDuration": 90,
                  "actionType": "INCREASE"
                }
                """;
    }
}