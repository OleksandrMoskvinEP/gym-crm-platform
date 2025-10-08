package com.gym.crm.integration.crosservice.steps;

import com.gym.crm.integration.crosservice.CrosserviceTestConfiguration;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.test.context.ContextConfiguration;

@ContextConfiguration(classes = CrosserviceTestConfiguration.class)
public class ManagePermissionsSteps {
    @When("I retrieve the trainee profile for the registered username")
    public void i_retrieve_the_trainee_profile_for_the_registered_username() {

    }

    @Then("the profile response should contain code {int} and the trainee's first name {string}")
    public void theProfileResponseShouldContainCodeAndTheTraineeSFirstName(int arg0, String arg1) {

    }

    @When("I attempt to retrieve the trainee profile for username {string} without authentication")
    public void i_attempt_to_retrieve_the_trainee_profile_for_username_without_authentication(String string) {

    }

    @Then("the error code should be {int} and  message should contain {string}")
    public void theErrorCodeShouldBeAndMessageShouldContain(int arg0, String arg1) {

    }
}
