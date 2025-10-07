@integration

Feature: Basic Cucumber check
  This feature exists only to verify that Cucumber and Spring context start correctly.

  Feature: Math operations
  Scenario: Checking math skills
    Given I have numbers 10 and 20 and 30
    When I operate with them
    Then the result should be grater than 0