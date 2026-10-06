package com.nishitweltech.automation.pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.options.WaitForSelectorState;

public class SignInPage extends BasePage {
    private static final String EMAIL = "input[placeholder='E mail']";
    private static final String PASSWORD = "input[placeholder='Password']";
    private static final String SIGN_IN_BUTTON = "#enterbtn";
    private static final String ERROR_MESSAGE = "#errormsg";

    public void signIn(String email, String password) {
        fill(EMAIL, email);
        fill(PASSWORD, password);
        click(SIGN_IN_BUTTON);
    }

    public void waitForInvalidCredentialsMessage() {
        page.locator(ERROR_MESSAGE).waitFor(
                new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE));
    }

    public String getInvalidCredentialsMessage() {
        return text(ERROR_MESSAGE);
    }
}
