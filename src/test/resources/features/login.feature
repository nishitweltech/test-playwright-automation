Feature: Sign in

  The demo application exposes a Sign In flow from its landing page.


  Scenario: Invalid credentials are rejected
    Given I open the demo application's landing page
    When I choose Sign In
    And I submit invalid sign-in credentials
    Then the invalid credentials message is displayed
