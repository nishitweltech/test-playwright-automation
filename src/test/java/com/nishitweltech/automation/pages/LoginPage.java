package com.nishitweltech.automation.pages;

import com.nishitweltech.automation.utils.ConfigReader;

public class LoginPage extends BasePage {
    private static final String USERNAME = "[data-test='username']";
    private static final String PASSWORD = "[data-test='password']";
    private static final String LOGIN_BUTTON = "[data-test='login-button']";
    private static final String INVENTORY_CONTAINER = "[data-test='inventory-container']";
    private static final String ERROR_MESSAGE = "[data-test='error']";

    public void open() {
        navigate(ConfigReader.get("baseUrl"));
    }

    public void login(String username, String password) {
        fill(USERNAME, username);
        fill(PASSWORD, password);
        click(LOGIN_BUTTON);
    }

    public boolean isInventoryDisplayed() {
        return page.locator(INVENTORY_CONTAINER).isVisible();
    }

    public String getErrorMessage() {
        return text(ERROR_MESSAGE);
    }
}
