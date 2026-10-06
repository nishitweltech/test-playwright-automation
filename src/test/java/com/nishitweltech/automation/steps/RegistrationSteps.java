package com.nishitweltech.automation.steps;

import com.nishitweltech.automation.pages.LandingPage;
import com.nishitweltech.automation.pages.RegistrationPage;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class RegistrationSteps {
    private final LandingPage landingPage = new LandingPage();
    private RegistrationPage registrationPage;

    @Given("I open the registration page")
    public void iOpenTheRegistrationPage() {
        landingPage.open();
        landingPage.openRegistration();
        registrationPage = new RegistrationPage();
        registrationPage.version();
    }

    @Then("the registration form marks its required fields")
    public void theRegistrationFormMarksItsRequiredFields() {
        assertTrue("Expected the registration form's mandatory fields to be required",
                registrationPage.hasRequiredFields());
    }

    @When("I enter passwords that do not match")
    public void iEnterPasswordsThatDoNotMatch() {
        registrationPage.enterMismatchedPasswords("GoodPass1", "DifferentPass2");
    }

    @Then("the password confirmation is rejected")
    public void thePasswordConfirmationIsRejected() {
        assertEquals("Passwords dont match",
                registrationPage.getConfirmPasswordValidationMessage());
    }
}
