package com.gym.crm.integration.core.steps;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.jupiter.api.Assertions;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Map;

import static org.hamcrest.Matchers.emptyOrNullString;
import static org.hamcrest.Matchers.not;

@SpringBootTest
public class CoreSteps {
    private static final String LOGIN_ENDPOINT = "/api/core/v1/login";
    private static final String TRAINEES_ENDPOINT = "/api/core/v1/trainees";
    private static final String TRAINING_TYPES_ENDPOINT = "/api/core/v1/training-types";

    private static String jwtToken;

    private Response lastResponse;

    @Given("a user {string} with password {string} exists")
    public void a_user_with_password_exists(String username, String password) {
        Assertions.assertNotNull(username);
        Assertions.assertNotNull(password);
    }

    @When("I authenticate with username {string} and password {string}")
    public void i_authenticate_with_username_and_password(String username, String password) {
        lastResponse = RestAssured.given()
                .basePath(LOGIN_ENDPOINT)
                .contentType(ContentType.JSON)
                .body(Map.of("username", username, "password", password))
                .when()
                .post()
                .then()
                .statusCode(200)
                .extract()
                .response();
    }

    @Then("I receive a valid JWT token")
    public void i_receive_a_valid_jwt_token() {
        jwtToken = lastResponse.then()
                .statusCode(200)
                .body("accessToken", not(emptyOrNullString()))
                .extract()
                .path("accessToken");
    }

    @When("I attempt to authenticate with wrong password {string}")
    public void iAttemptToAuthenticateWithWrongPassword(String wrongPassword) {
        lastResponse = RestAssured.given()
                .basePath(LOGIN_ENDPOINT)
                .contentType(ContentType.JSON)
                .body(Map.of(
                        "username", "arnold.schwarzenegger",
                        "password", wrongPassword))
                .when()
                .post()
                .then()
                .extract()
                .response();
    }

    @Then("I should receive a response with status code {int}")
    public void i_should_receive_a_response_with_status_code(Integer statusCode) {
        lastResponse.then().statusCode(statusCode);
    }

    @Given("a trainee with username {string} exists")
    public void a_trainee_with_username_exists(String string) {
    }

    @When("I request the trainee profile by username {string}")
    public void i_request_the_trainee_profile_by_username(String string) {
        //отправить get запрос на localhost:8081/api/core/v1/trainees/{username}
        //в заголовке Authorization должен быть токен полученный при аутентификации
    }

    @Then("I should retrieve a trainee profile with first name {string} and last name {string}")
    public void i_should_retrieve_a_trainee_profile_with_first_name_and_last_name(String string, String string2) {
        //проверить что в ответе есть поля firstName и lastName и они равны ожидаемым значениям
    }

    @When("I request the list of training types")
    public void i_request_the_list_of_training_types() {
        //отправить get запрос на localhost:8081/api/core/v1/training-types
        //в заголовке Authorization должен быть токен полученный при аутентификации
    }

    @Then("I should receive {int} training types")
    public void i_should_receive_training_types(Integer int1) {
        //проверить что в ответе массив с типами тренировок и его размер равен int1
    }
}
