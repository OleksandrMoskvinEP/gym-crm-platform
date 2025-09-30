package it.steps;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class CoreSteps {
    private boolean isOk;

    @Given("core precondition is met")
    public void core_precondition_is_met() {
        isOk = true;
    }

    @When("core action is performed")
    public void core_action_is_performed() {
        if (!isOk) {
            throw new IllegalStateException("Everything must be Ok!");
        }
    }

    @Then("core postcondition is verified")
    public void core_postcondition_is_verified() {
        assertTrue(isOk);
    }
}
