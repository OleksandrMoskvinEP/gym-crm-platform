package com.gym.crm.integration.crosservice.steps;

import com.gym.crm.integration.crosservice.CrosserviceTestConfiguration;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.springframework.test.context.ContextConfiguration;

import java.util.Map;

import static org.hamcrest.Matchers.equalTo;

@ContextConfiguration(classes = CrosserviceTestConfiguration.class)
public class ManagePermissionsSteps {
    private static final String TRAINEES_BASE_ENDPOINT = "/api/core/v1/trainees";
    private static final String LOGIN_ENDPOINT = "/api/core/v1/login";
    private static final String GATEWAY_URI = CrosserviceTestConfiguration.getGATEWAY_URL();

    private Response lastResponse;
    private String jwtToken;

    @When("I retrieve the trainee profile for the registered username")
    public void i_retrieve_the_trainee_profile_for_the_registered_username() {
        ensureAuthenticated("arnold.schwarzenegger", "qwerty1234");

        lastResponse = RestAssured.given()
                .baseUri(GATEWAY_URI)
                .basePath(TRAINEES_BASE_ENDPOINT + "/max.pereira")
                .header("Authorization", "Bearer " + jwtToken)
                .when()
                .get()
                .then()
                .extract()
                .response();
    }

    @Then("the profile response should contain code {int} and the trainee's first name {string}")
    public void theProfileResponseShouldContainCodeAndTheTraineeSFirstName(int expectedStatusCode, String firstName) {
        lastResponse.then()
                .statusCode(expectedStatusCode)
                .body("firstName", equalTo(firstName));
    }

    @When("I attempt to retrieve the trainee profile for username {string} without authentication")
    public void i_attempt_to_retrieve_the_trainee_profile_for_username_without_authentication(String username) {
        jwtToken = null;

        lastResponse = RestAssured.given()
                .baseUri(GATEWAY_URI)
                .basePath(TRAINEES_BASE_ENDPOINT + username)
                .header("Authorization", "Bearer " + jwtToken)
                .when()
                .get()
                .then()
                .extract()
                .response();
    }

    @Then("the error code should be {int}")
    public void theErrorCodeShouldBe(int expectedErrorCode) {
       lastResponse.then()
                .statusCode(expectedErrorCode);
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
}