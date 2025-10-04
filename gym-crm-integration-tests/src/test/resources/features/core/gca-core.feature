@core

Feature: Trainee,Trainer and Trainings Management

  @PositiveCase
  Scenario: Successful authentication of an existing user
    Given a user "arnold.schwarzenegger" with password "qwerty1234" exists
    When I authenticate with username "arnold.schwarzenegger" and password "qwerty1234"
    Then I receive a valid JWT token

  @NegativeCase
  Scenario: Failed authentication with incorrect password
    When I attempt to authenticate with wrong password "wrongpassword"
    Then I should receive a response with status code 401

  Scenario: Find trainee profile by valid username
    Given a trainee with username "olga.ivanova" exists
    When I request the trainee profile by username "olga.ivanova"
    Then I should retrieve a trainee profile with first name "Olga" and last name "Ivanova"

  Scenario: Get available training types
    When I request the list of training types
    Then I should receive 6 training types

