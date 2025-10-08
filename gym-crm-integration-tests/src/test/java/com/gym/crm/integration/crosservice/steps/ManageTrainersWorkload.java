package com.gym.crm.integration.crosservice.steps;

import com.gym.crm.integration.crosservice.CrosserviceTestConfiguration;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.jupiter.api.Assertions;
import org.springframework.test.context.ContextConfiguration;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Date;
import java.util.Map;

import static org.hamcrest.Matchers.emptyOrNullString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.not;

@ContextConfiguration(classes = CrosserviceTestConfiguration.class)
public class ManageTrainersWorkload {
    private static final String WORKLOAD_SECRET = "AnotherSecuredSecretKeyForWorkloadService123456";
    private static final String LOGIN_ENDPOINT = "/api/core/v1/login";
    private static final String GATEWAY_URI = CrosserviceTestConfiguration.getGATEWAY_URL();
    private static final String WORKLOAD_URI = CrosserviceTestConfiguration.getWORKLOAD_URL();

    private static String jwtToken;

    private Response lastResponse;
    private Map<String, String> credentials;
    private Map<String, String> wrongCredentials;

    @Given("I have correct user credentials")
    public void i_have_correct_user_credentials(DataTable dataTable) {
        credentials = dataTable.asMap(String.class, String.class);

        ensureAuthenticated(credentials.get("username"), credentials.get("password"));

        Assertions.assertNotNull(jwtToken, "Failed to authenticate as trainer");
    }

    @When("I send a request for login to core-service")
    public void i_send_a_request_for_login_to_core_service() {
        lastResponse = RestAssured.given()
                .baseUri(GATEWAY_URI)
                .basePath(LOGIN_ENDPOINT)
                .contentType(ContentType.JSON)
                .body(credentials)
                .when()
                .post()
                .then()
                .statusCode(200)
                .extract()
                .response();
    }

    @Then("I should receive a valid JWT token")
    public void i_should_receive_a_valid_jwt_token() {
        jwtToken = lastResponse.then()
                .statusCode(200)
                .body("accessToken", not(emptyOrNullString()))
                .extract()
                .path("accessToken");
    }

    @When("I register a new training for a trainer with gym-crm-service")
    public void i_register_a_new_training_for_a_trainer_with_gym_crm_service(DataTable dataTable) {
        Map<String, String> trainingData = dataTable.asMap(String.class, String.class);

        Map<String, Object> trainingRequest = Map.of(
                "traineeUsername", trainingData.get("traineeUsername"),
                "trainerUsername", trainingData.get("trainerUsername"),
                "trainingName", trainingData.get("trainingName"),
                "trainingDate", trainingData.get("trainingDate"),
                "trainingDuration", Integer.parseInt(trainingData.get("trainingDuration"))
        );

        lastResponse = RestAssured.given()
                .baseUri(GATEWAY_URI)
                .basePath("/api/core/v1/trainings")
                .header("Authorization", "Bearer " + jwtToken)
                .contentType(ContentType.JSON)
                .body(trainingRequest)
                .when()
                .post()
                .then()
                .statusCode(200)
                .extract()
                .response();
    }

    @When("I attempt to retrieve the trainings  details from workload-service")
    public void i_attempt_to_retrieve_the_trainings_details_from_workload_service(DataTable dataTable) {
        Map<String, String> workloadParams = dataTable.asMap(String.class, String.class);

        String username = workloadParams.get("trainerUsername");
        String year = workloadParams.get("year");
        String month = workloadParams.get("month");

        lastResponse = RestAssured.given()
                .baseUri(WORKLOAD_URI)
                .header("Authorization", "Bearer " + generateServiceToken())
                .accept(ContentType.JSON)
                .when()
                .get("/api/v1/trainers-workload/{username}/{year}/{month}", username, year, month)
                .then()
                .extract()
                .response();
    }

    @Then("I can verify the trainings details with workload-service")
    public void i_can_verify_the_trainings_details_with_workload_service(DataTable dataTable) {
        Map<String, String> data = dataTable.asMap(String.class, String.class);
        Assertions.assertNotNull(lastResponse);

        lastResponse.then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("totalHours", equalTo(Integer.parseInt(data.get("totalHours"))))
                .body("totalMinutes", equalTo(Integer.parseInt(data.get("totalMinutes"))))
                .body("year", equalTo(Integer.parseInt(data.get("year"))))
                .body("month", equalTo(Integer.parseInt(data.get("month"))))
                .body("username", equalTo(data.get("trainerUsername")));
    }

    @Given("I have wrong user credentials")
    public void iHaveWrongUserCredentials(DataTable dataTable) {
        wrongCredentials = dataTable.asMap(String.class, String.class);
    }

    @When("I try to login to core-service")
    public void i_try_to_login_to_core_service() {
        jwtToken = null;

        lastResponse = RestAssured.given()
                .baseUri(GATEWAY_URI)
                .basePath(LOGIN_ENDPOINT)
                .contentType(ContentType.JSON)
                .body(wrongCredentials)
                .when()
                .post()
                .then()
                .extract()
                .response();
    }

    @Then("I see a handled exception")
    public void iSeeAHandledException(DataTable dataTable) {
        Map<String, String> data = dataTable.asMap(String.class, String.class);

        lastResponse.then()
                .body("errorCode", equalTo(Integer.parseInt(data.get("customErrorCode"))))
                .body("errorMessage", equalTo(data.get("message")));
    }

    private void ensureAuthenticated(String username, String password) {
        if (jwtToken != null) {
            return;
        }

        Response response = RestAssured.given()
                .baseUri(GATEWAY_URI)
                .basePath(LOGIN_ENDPOINT)
                .contentType(ContentType.JSON)
                .body(Map.of("username", username, "password", password))
                .when()
                .post()
                .then()
                .statusCode(200)
                .extract()
                .response();

        jwtToken = response.path("accessToken");
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
}