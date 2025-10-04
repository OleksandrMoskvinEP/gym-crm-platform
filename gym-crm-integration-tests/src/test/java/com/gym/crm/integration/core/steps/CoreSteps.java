package com.gym.crm.integration.core.steps;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class CoreSteps {
    @Given("a user {string} with password {string} exists")
    public void a_user_with_password_exists(String string, String string2) {
    }
    @When("I authenticate with username {string} and password {string}")
    public void i_authenticate_with_username_and_password(String string, String string2) {
        //отправить post запрос на localhost:8081/api/core/v1/login
        //с телом запроса {
        // "username": "arnold.schwarzenegger",
        //  "password": "qwerty1234"
        //  }
    }
    @Then("I receive a valid JWT token")
    public void i_receive_a_valid_jwt_token() {
        //проверить что в ответе есть поле token и оно не пустое
        //сохранить токен в переменную
    }
    @When("I attempt to authenticate with wrong password {string}")
    public void iAttemptToAuthenticateWithWrongPassword(String arg0) {
        //отправить post запрос на localhost:8081/api/core/v1/login
        //с телом запроса {
        // "username": "arnold.schwarzenegger",
        //  "password": "qwerty"
        //  }
    }
    @Then("I should receive a response with status code {int}")
    public void i_should_receive_a_response_with_status_code(Integer int1) {
    }
    @Then("the error message should contain {string}")
    public void the_error_message_should_contain(String string) {
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
