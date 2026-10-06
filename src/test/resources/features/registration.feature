Feature: Registration

  The registration form validates required data and matching passwords in the browser.

  Background:
    Given I open the registration page

  @smoke @registration
  Scenario: Required registration fields are configured
    Then the registration form marks its required fields

  @registration
  Scenario: Password confirmation must match
    When I enter passwords that do not match
    Then the password confirmation is rejected
