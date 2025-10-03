package com.gym.crm.integration.steps;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

public class WorkloadSteps{
    @Given("the trainer arnold_schwarzenegger exists and has a total training duration of {int}")
    public void the_trainer_arnold_schwarzenegger_exists_and_has_a_total_training_duration_of(Integer int1) {

    }
    @When("I request the workload for trainer {string} year {int} month {int}")
    public void i_request_the_workload_for_trainer_year_month(String string, Integer int1, Integer int2) {

    }
    @Then("the workload returned should be {int}")
    public void the_workload_returned_should_be(Integer int1) {

    }

    @Given("no trainer exists with username {string}")
    public void no_trainer_exists_with_username(String string) {

    }
    @When("I request the workload for trainer {string} for year {int} month {int}")
    public void i_request_the_workload_for_trainer_for_year_month(String string, Integer int1, Integer int2) {

    }
    @Then("the response status should be {int}")
    public void the_response_status_should_be(Integer int1) {

    }
    @Then("the error message should contain {string}")
    public void the_error_message_should_contain(String string) {

    }
}
