package com.gym.crm.integration.crosservice.steps;

import com.gym.crm.integration.crosservice.CrosserviceTestConfiguration;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.test.context.ContextConfiguration;

@ContextConfiguration(classes = CrosserviceTestConfiguration.class)
public class Check2Steps {
    private int a;
    private int b;
    private int sum;

    @Given("I have numbers {int} and {int}")
    public void iHaveNumbersAnd(int arg0, int arg1) {
        this.a = arg0;
        this.b = arg1;
    }

    @When("I add them")
    public void iAddThem() {
        sum = a + b;
    }

    @Then("the result should be {int}")
    public void the_result_should_be(int expectedSum) {
        if (this.sum != expectedSum) {
            throw new AssertionError("Expected sum: " + expectedSum + ", but got: " + this.sum);
        }
    }
}
