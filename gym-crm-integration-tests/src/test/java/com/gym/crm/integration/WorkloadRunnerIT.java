package com.gym.crm.integration;

import org.junit.platform.suite.api.ConfigurationParameter;
import org.junit.platform.suite.api.IncludeEngines;
import org.junit.platform.suite.api.SelectClasspathResource;
import org.junit.platform.suite.api.Suite;

@Suite
@IncludeEngines("cucumber")
@SelectClasspathResource("features/workload")
@ConfigurationParameter(key = "cucumber.glue", value = "com.gym.crm.integration.workload")
public class WorkloadRunnerIT {
}