package com.gym.crm.integration.crosservice.steps;

import com.gym.crm.integration.crosservice.CrosserviceTestConfiguration;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.test.context.ContextConfiguration;

@ContextConfiguration(classes = CrosserviceTestConfiguration.class)
public class Check3Steps {
    private int a, b, c, result;

    @Given("I have numbers {int} and {int} and {int}")
    public void i_have_numbers_and_and(Integer int1, Integer int2, Integer int3) {
        this.a = int1;
        this.b = int2;
        this.c = int3;
    }

    @When("I operate with them")
    public void i_operate_with_them() {
        result = (a + b) / c;
    }

    @Then("the result should be grater than {int}")
    public void the_result_should_be_grater_than(Integer int1) throws InterruptedException {
        if (result <= int1) {
            throw new AssertionError("The result should be grater than " + int1);
        }
    }
}
