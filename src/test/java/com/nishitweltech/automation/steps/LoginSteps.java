package com.nishitweltech.automation.steps;

import com.nishitweltech.automation.pages.LoginPage;
import com.nishitweltech.automation.utils.ConfigReader;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import static org.junit.Assert.assertTrue;

public class LoginSteps {
    private LoginPage loginPage;

    @Given("I am on the Sauce Demo login page")
    public void iAmOnTheLoginPage() {
        loginPage = new LoginPage();
        loginPage.open();
    }

    @When("I login with the standard test user")
    public void iLoginWithTheStandardTestUser() {
        loginPage.login(ConfigReader.get("username"), ConfigReader.get("password"));
    }

    @Then("the products page should be displayed")
    public void theProductsPageShouldBeDisplayed() {
        assertTrue("Products page was not displayed", loginPage.isInventoryDisplayed());
    }
}
