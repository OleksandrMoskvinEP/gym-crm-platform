package com.gym.crm.integration.crosservice.steps;

import com.gym.crm.integration.crosservice.CrosserviceTestConfiguration;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.jupiter.api.Assertions;
import org.springframework.test.context.ContextConfiguration;

import java.util.Map;

import static org.hamcrest.Matchers.emptyOrNullString;
import static org.hamcrest.Matchers.not;

@ContextConfiguration(classes = CrosserviceTestConfiguration.class)
public class ManageTrainersWorkload {
    private static final String LOGIN_ENDPOINT = "/api/core/v1/login";
    private static final String GATEWAY_URI = CrosserviceTestConfiguration.getGATEWAY_URL();
    private static final String WORKLOAD_URI = CrosserviceTestConfiguration.getWORKLOAD_URL();

    private String jwtToken;
    private Response lastResponse;
    private Map<String, String> credentials;

    @Given("I have correct user credentials")
    public void i_have_correct_user_credentials(DataTable dataTable) throws InterruptedException {
        credentials = dataTable.asMap(String.class, String.class);

        ensureAuthenticated(credentials.get("username"), credentials.get("password"));

        Assertions.assertNotNull(jwtToken, "Failed to authenticate as trainer");

    }

    @When("I send a request for authentication to authentication-service")
    public void i_send_a_request_for_authentication_to_authentication_service() {
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

    @When("I register a new training session for a trainer with gym-crm-service")
    public void i_register_a_new_training_session_for_a_trainer_with_gym_crm_service(DataTable dataTable) {

    }

    @When("I attempt to retrieve the trainings  details from workload-service")
    public void i_attempt_to_retrieve_the_trainings_details_from_workload_service() {

    }

    @Then("I can verify the trainings details with workload-service")
    public void i_can_verify_the_trainings_details_with_workload_service() {

    }

    private void ensureAuthenticated(String username, String password) throws InterruptedException {
        if (jwtToken != null) {
            return;
        }
        System.out.println(GATEWAY_URI +LOGIN_ENDPOINT+"================================================");
         Thread.sleep(10*5000);

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
}
