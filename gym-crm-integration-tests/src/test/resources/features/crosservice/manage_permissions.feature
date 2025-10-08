@integration

Feature: Trainee Profile Retrieval
  As an authenticated trainee
  I want to retrieve my profile information
  So that I can view my assigned trainers and details

  @PositiveCase
  Scenario: Successfully create and retrieve trainee profile with valid authentication
    Given I register a new trainee with the following details:
      | firstName | lastName | dateOfBirth | address         |
      | Max       | Pereira  | 1990-01-01  | 123 Main Street |
    When I login with the registered trainee credentials
    Then the response status code should be 200

    When I retrieve the trainee profile for the registered username
    Then the profile response should contain code 200 and the trainee's first name "Max"

  @NegativeCase
  Scenario: Failed to retrieve trainee profile due to unauthorized access
    When I attempt to retrieve the trainee profile for username "chama.chama" without authentication
    Then the error code should be 401 and  message should contain "error massage"