Feature: Sauce Demo login

  As a registered user
  I want to log in
  So that I can access the products page

  @smoke @login
  Scenario: Successful login with standard user
    Given I am on the Sauce Demo login page
    When I login with the standard test user
    Then the products page should be displayed
