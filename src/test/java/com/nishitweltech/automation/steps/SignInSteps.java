package com.nishitweltech.automation.steps;

import com.nishitweltech.automation.pages.LandingPage;
import com.nishitweltech.automation.pages.SignInPage;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import static org.junit.Assert.assertEquals;

public class SignInSteps {
    private final LandingPage landingPage = new LandingPage();
    private final SignInPage signInPage = new SignInPage();

    @Given("I open the demo application's landing page")
    public void iOpenTheDemoApplicationsLandingPage() {
        landingPage.open();
    }

    @When("I choose Sign In")
    public void iChooseSignIn() {
        landingPage.openSignIn();
    }

    @When("I submit invalid sign-in credentials")
    public void iSubmitInvalidSignInCredentials() {
        signInPage.signIn("invalid.user@example.com", "IncorrectPassword1");
    }

    @Then("the invalid credentials message is displayed")
    public void theInvalidCredentialsMessageIsDisplayed() {
        signInPage.waitForInvalidCredentialsMessage();
        assertEquals("Invalid User Name or PassWord",
                signInPage.getInvalidCredentialsMessage().trim());
    }
}
