Feature: Basic Cucumber check
  This feature exists only to verify that Cucumber and Spring context start correctly.

  Feature: Simple addition

  Scenario: Add two numbers
    Given I have numbers 5 and 7
    When I add them
    Then the result should be 12
