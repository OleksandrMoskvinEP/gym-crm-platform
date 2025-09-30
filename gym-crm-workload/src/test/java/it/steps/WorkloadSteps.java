package it.steps;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class WorkloadSteps {
    private boolean isOk;

    @Given("workload precondition is met")
    public void workload_precondition_is_met() {
        isOk = true;
    }

    @When("workload action is performed")
    public void workload_action_is_performed() {
        if (!isOk) {
            throw new IllegalStateException("Everything must be Ok!");
        }
    }

    @Then("workload postcondition is verified")
    public void workload_postcondition_is_verified() {
        assertTrue(isOk);
    }
}
