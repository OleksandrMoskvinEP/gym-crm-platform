@integration

Feature: Trainer hours registration and verification

  Scenario: Register and verify trainer's workload hours
    Given I have correct user credentials
      | username | arnold.schwarzenegger |
      | password | qwerty1234            |
    When I send a request for authentication to authentication-service
    Then I should receive a valid JWT token
    When I register a new training session for a trainer with gym-crm-service
      | traineeUsername  | john.smith            |
      | trainerUsername  | arnold.schwarzenegger |
      | trainingName     | Cycling               |
      | trainingDate     | 2025-07-27            |
      | trainingDuration | 120                   |
    And I attempt to retrieve the trainings  details from workload-service
    Then I can verify the trainings details with workload-service