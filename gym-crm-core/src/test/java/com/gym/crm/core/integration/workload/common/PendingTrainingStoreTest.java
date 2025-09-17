package com.gym.crm.core.integration.workload.common;

import com.gym.crm.core.domain.dto.training.TrainingSaveRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class PendingTrainingStoreTest {
    private PendingTrainingStore pendingTrainingStore;

    @BeforeEach
    void setUp() {
        pendingTrainingStore = new PendingTrainingStore();
    }

    @Test
    void shouldStoreTrainingRequest() {
        String correlationId = "test-correlation-id";
        TrainingSaveRequest request = createTrainingSaveRequest();

        pendingTrainingStore.put(correlationId, request);

        Optional<TrainingSaveRequest> retrieved = pendingTrainingStore.remove(correlationId);
        assertTrue(retrieved.isPresent());
        assertEquals(request, retrieved.get());
    }

    @Test
    void shouldReturnEmptyForNonExistentCorrelationId() {
        String nonExistentId = "non-existent-id";

        Optional<TrainingSaveRequest> result = pendingTrainingStore.remove(nonExistentId);

        assertFalse(result.isPresent());
    }

    @Test
    void shouldRemoveTrainingRequestAfterRetrieval() {
        String correlationId = "test-correlation-id";
        TrainingSaveRequest request = createTrainingSaveRequest();

        pendingTrainingStore.put(correlationId, request);

        Optional<TrainingSaveRequest> firstRetrieval = pendingTrainingStore.remove(correlationId);
        Optional<TrainingSaveRequest> secondRetrieval = pendingTrainingStore.remove(correlationId);

        assertTrue(firstRetrieval.isPresent());
        assertEquals(request, firstRetrieval.get());
        assertFalse(secondRetrieval.isPresent());
    }

    @Test
    void put_shouldOverwriteExistingCorrelationId() {
        String correlationId = "test-correlation-id";
        TrainingSaveRequest firstRequest = createTrainingSaveRequest();
        TrainingSaveRequest secondRequest = createTrainingSaveRequest();
        secondRequest.setTrainingName("Different Training");

        pendingTrainingStore.put(correlationId, firstRequest);
        pendingTrainingStore.put(correlationId, secondRequest);

        Optional<TrainingSaveRequest> retrieved = pendingTrainingStore.remove(correlationId);

        assertTrue(retrieved.isPresent());
        assertEquals(secondRequest, retrieved.get());
        assertEquals("Different Training", retrieved.get().getTrainingName());
    }

    @Test
    void concurrentAccess_shouldHandleMultipleThreads() throws InterruptedException {
        int numberOfThreads = 10;
        int requestsPerThread = 100;
        Thread[] threads = new Thread[numberOfThreads];

        for (int i = 0; i < numberOfThreads; i++) {
            final int threadIndex = i;

            threads[i] = new Thread(() -> {
                for (int j = 0; j < requestsPerThread; j++) {
                    String correlationId = "thread-" + threadIndex + "-request-" + j;

                    TrainingSaveRequest request = createTrainingSaveRequest();
                    request.setTrainingName("Training from thread " + threadIndex + " request " + j);

                    pendingTrainingStore.put(correlationId, request);
                    Optional<TrainingSaveRequest> retrieved = pendingTrainingStore.remove(correlationId);

                    assertTrue(retrieved.isPresent());
                    assertEquals(request, retrieved.get());
                }
            });
        }

        for (Thread thread : threads) {
            thread.start();
            thread.join();
        }

        for (int i = 0; i < numberOfThreads; i++) {
            for (int j = 0; j < requestsPerThread; j++) {
                String correlationId = "thread-" + i + "-request-" + j;
                Optional<TrainingSaveRequest> retrieved = pendingTrainingStore.remove(correlationId);

                assertFalse(retrieved.isPresent(), "Request should not exist after removal");
            }
        }
    }

    private TrainingSaveRequest createTrainingSaveRequest() {
        TrainingSaveRequest request = new TrainingSaveRequest();
        request.setTrainingName("Test Training");
        request.setTrainingDate(LocalDate.of(2025, 1, 15));
        request.setTrainingDuration(BigDecimal.valueOf(60));
        request.setTrainingTypeName("Strength Training");
        request.setTraineeId(1L);
        request.setTrainerId(2L);

        return request;
    }
}