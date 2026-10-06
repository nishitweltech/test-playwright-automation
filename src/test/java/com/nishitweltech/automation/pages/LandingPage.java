package com.nishitweltech.automation.pages;

import com.nishitweltech.automation.utils.ConfigReader;

public class LandingPage extends BasePage {
    private static final String SIGN_IN_BUTTON = "#btn1";
    private static final String SKIP_SIGN_IN_BUTTON = "#btn2";

    public void open() {
        navigate(ConfigReader.get("baseUrl"));
    }

    public void openSignIn() {
        click(SIGN_IN_BUTTON);
    }

    public void openRegistration() {
        click(SKIP_SIGN_IN_BUTTON);
    }
}
