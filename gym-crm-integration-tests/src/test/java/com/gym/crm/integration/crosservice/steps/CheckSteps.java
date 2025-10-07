package com.gym.crm.integration.crosservice.steps;

import com.gym.crm.integration.crosservice.CrosserviceTestConfiguration;
import io.cucumber.java.en.Given;
import org.springframework.test.context.ContextConfiguration;

@ContextConfiguration(classes = CrosserviceTestConfiguration.class)
public class CheckSteps {
    @Given("system is up")
    public void system_is_up() {
    }
}
