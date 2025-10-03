package com.gym.crm.integration.steps;

import com.gym.crm.integration.utills.WorkloadSender;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;

public class WorkloadSteps {
    private static final String TYPE_ID = "com.gym.crm.core.integration.workload.dto.WorkloadEventRequest";
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE;

    @Autowired
    private WorkloadSender workloadSender;
    private Response lastResponse;

    @Given("the trainer arnold_schwarzenegger exists and has a total training duration of {int}")
    public void the_trainer_arnold_schwarzenegger_exists_and_has_a_total_training_duration_of(Integer duration) {
        LocalDate trainingDate = LocalDate.of(2025, 10, 1);
        sendWorkloadEvent(trainingDate, duration);

      //  waitForTrainerWorkload("arnold_schwarzenegger", 2025, 10, duration / 60);
    }

    @When("I request the workload for trainer {string} year {int} month {int}")
    public void i_request_the_workload_for_trainer_year_month(String username, Integer year, Integer month) {
        lastResponse = requestWorkload(username, year, month);

    }

    @Then("the workload returned should be {int}")
    public void the_workload_returned_should_be(Integer expectedHours) {
        lastResponse.then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("totalHours", equalTo(expectedHours))
                .body("totalMinutes", equalTo(expectedHours * 60));
    }

    @Given("no trainer exists with username {string}")
    public void no_trainer_exists_with_username(String string) {
        lastResponse = null;
    }

    @When("I request the workload for trainer {string} for year {int} month {int}")
    public void i_request_the_workload_for_trainer_for_year_month(String username, Integer year, Integer month) {
        lastResponse = requestWorkload(username, year, month);
    }

    @Then("the response status should be {int}")
    public void the_response_status_should_be(Integer expectedStatus) {
        lastResponse.then().statusCode(expectedStatus);
    }

    @Then("the error message should contain {string}")
    public void the_error_message_should_contain(String expectedMessage) {
        lastResponse.then()
                .contentType(ContentType.JSON)
                .body("message", containsString(expectedMessage));
    }

    private void sendWorkloadEvent(LocalDate trainingDate,
                                   Integer trainingDuration) {
        Map<String, Object> payload = new HashMap<>();

        payload.put("username", "arnold_schwarzenegger");
        payload.put("firstName", "Arnold");
        payload.put("lastName", "Schwarzenegger");
        payload.put("isActive", true);
        payload.put("trainingDate", trainingDate.format(DATE_FORMATTER));
        payload.put("trainingDuration", trainingDuration);
        payload.put("actionType", "ADD");

        workloadSender.send(payload, TYPE_ID);
    }

    private void waitForTrainerWorkload(String username, int year, int month, int expectedHours) {
        long timeoutMillis = Duration.ofSeconds(600).toMillis();
        long start = System.currentTimeMillis();

        while (System.currentTimeMillis() - start < timeoutMillis) {
            Response response = requestWorkload(username, year, month);
            if (response.statusCode() == 200) {
                int totalHours = response.jsonPath().getInt("totalHours");
                if (totalHours == expectedHours) {
                    return;
                }
            }

            sleep(500);
        }

        throw new AssertionError("Trainer workload was not available within the expected time");
    }

    private void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();

            throw new RuntimeException("Waiting for workload data was interrupted", e);
        }
    }

    private Response requestWorkload(String username, Integer year, Integer month) {
        return RestAssured.given()
                .accept(ContentType.JSON)
                .when()
                .get("/api/v1/trainers-workload/{username}/{year}/{month}", username, year, month)
                .then()
                .extract()
                .response();
    }
}
