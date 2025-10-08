package com.gym.crm.integration.workload.steps;

import com.gym.crm.integration.utills.WorkloadSender;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.awaitility.Awaitility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;

@SpringBootTest
public class GcaWorkloadSteps {
    private static final String WORKLOAD_SECRET = "AnotherSecuredSecretKeyForWorkloadService123456";
    private static final String TYPE_ID = "com.gym.crm.core.integration.workload.dto.WorkloadEventRequest";
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE;

    @Autowired
    private WorkloadSender workloadSender;
    private Response lastResponse;

    @Given("the trainer arnold_schwarzenegger exists and has a total training duration of {int}")
    public void the_trainer_arnold_schwarzenegger_exists_and_has_a_total_training_duration_of(Integer duration) {
        LocalDate trainingDate = LocalDate.of(2025, 10, 1);
        sendWorkloadEvent(trainingDate, duration);
    }

    @When("I request the workload for trainer {string} year {int} month {int}")
    public void i_request_the_workload_for_trainer_year_month(String username, Integer year, Integer month) {
        lastResponse = requestWorkloadWithRetry(username, year, month);
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

    private Response requestWorkload(String username, Integer year, Integer month) {
        return RestAssured.given()
                .header("Authorization", "Bearer " + generateServiceToken())
                .accept(ContentType.JSON)
                .when()
                .get("/api/v1/trainers-workload/{username}/{year}/{month}", username, year, month)
                .then()
                .extract()
                .response();
    }

    private String generateServiceToken() {
        SecretKey secretKey = Keys.hmacShaKeyFor(WORKLOAD_SECRET.getBytes(StandardCharsets.UTF_8));

        return Jwts.builder()
                .subject("core-service")
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + Duration.ofMinutes(5).toMillis()))
                .signWith(secretKey)
                .compact();
    }

    private Response requestWorkloadWithRetry(String username, Integer year, Integer month) {
        AtomicReference<Response> responseReference = new AtomicReference<>();

        Awaitility.await()
                .atMost(Duration.ofSeconds(10))
                .pollInterval(Duration.ofMillis(200))
                .until(() -> {
                    Response response = requestWorkload(username, year, month);

                    responseReference.set(response);

                    return response.statusCode() == 200;
                });

        return responseReference.get();
    }
}
