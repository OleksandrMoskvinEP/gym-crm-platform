@integration

Feature: Trainer hours registration and verification

  @PositiveCase
  Scenario: Register and verify trainer's workload hours
    Given I have correct user credentials
      | username | arnold.schwarzenegger |
      | password | qwerty1234            |
    When I send a request for login to core-service
    Then I should receive a valid JWT token
    When I register a new training for a trainer with gym-crm-service
      | traineeUsername  | john.smith            |
      | trainerUsername  | arnold.schwarzenegger |
      | trainingName     | Cycling               |
      | trainingDate     | 2025-07-27            |
      | trainingDuration | 120                   |
    And I attempt to retrieve the trainings  details from workload-service
      | trainerUsername | arnold.schwarzenegger |
      | year            | 2025                  |
      | month           | 7                     |
    Then I can verify the trainings details with workload-service
      | trainerUsername | arnold.schwarzenegger |
      | year            | 2025                  |
      | month           | 7                     |
      | totalMinutes    | 120                   |
      | totalHours      | 2                     |

  @NegativeCase
  Scenario: Try to login with wrong credentials
    Given I have wrong user credentials
      | username | arnold.schwarzenegger |
      | password | 1234                  |
    When I try to login to core-service
    Then I see a handled exception
      | customErrorCode | 2760                                             |
      | message         | password: size must be between 10 and 2147483647 |