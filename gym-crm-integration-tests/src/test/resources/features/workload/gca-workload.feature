@gca-workload
Feature: Trainer Workload Management

  @PositiveCase
  Scenario: Get correct workload for a given trainer, year, and month
    Given the trainer arnold_schwarzenegger exists and has a total training duration of 120
    When I request the workload for trainer "arnold_schwarzenegger" year 2025 month 10
    Then the workload returned should be 2

  @NegativeCase
  Scenario: Attempt to get workload for unknown trainer
    Given no trainer exists with username "rocky_balboa"
    When I request the workload for trainer "rocky_balboa" for year 2024 month 11
    Then the response status should be 400
    And the error message should contain "Trainer not found: rocky_balboa"