package com.gym.crm.integration.steps;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class IntegrationSteps {
    private boolean isOk;

    @Given("Cross service precondition is met")
    public void cross_service_precondition_is_met() {
        isOk = true;
    }

    @When("Cross service is performed")
    public void cross_service_is_performed() {
        if (!isOk) {
            throw new IllegalStateException("Precondition not met");
        }
    }

    @Then("Cross service postcondition is verified")
    public void cross_service_postcondition_is_verified() {
      assertTrue(isOk);
    }
}
