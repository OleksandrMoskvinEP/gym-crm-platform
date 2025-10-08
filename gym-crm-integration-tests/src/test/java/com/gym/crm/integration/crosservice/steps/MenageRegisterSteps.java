package com.gym.crm.integration.crosservice.steps;

import com.gym.crm.integration.crosservice.CrosserviceTestConfiguration;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.springframework.test.context.ContextConfiguration;

import java.time.LocalDate;
import java.util.Map;

import static io.restassured.RestAssured.given;

@ContextConfiguration(classes = CrosserviceTestConfiguration.class)
public class MenageRegisterSteps {
    private static final String TRAINEES_BASE_ENDPOINT = "/api/core/v1/trainees";
    private static final String GATEWAY_URI = CrosserviceTestConfiguration.getGATEWAY_URL();

    private Response lastResponse;
    private Map<String, Object> requestBody;

    @Given("I register a new trainee with the following details:")
    public void i_register_a_new_trainee_with_the_following_details(DataTable dataTable) {
        Map<String, String> data = dataTable.asMaps(String.class, String.class).get(0);

        Map<String, Object> user = Map.of(
                "firstName", data.get("firstName"),
                "lastName", data.get("lastName"),
                "isActive", true
        );
        requestBody = Map.of(
                "user", user,
                "dateOfBirth", LocalDate.parse(data.get("dateOfBirth")).toString(),
                "address", data.get("address")
        );
    }

    @When("I login with the registered trainee credentials")
    public void i_login_with_the_registered_trainee_credentials() {
        lastResponse = given()
                .baseUri(GATEWAY_URI)
                .basePath(TRAINEES_BASE_ENDPOINT + "/register")
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .post();
    }

    @Then("the response status code should be {int}")
    public void the_response_status_code_should_be(Integer expectedStatusCode) {
        lastResponse.then().statusCode(expectedStatusCode);
    }
}
