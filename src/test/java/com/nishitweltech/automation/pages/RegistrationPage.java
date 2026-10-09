package com.nishitweltech.automation.pages;

import com.nishitweltech.automation.utils.ConfigReader;

public class RegistrationPage extends BasePage {
    private static final String FIRST_NAME = "input[placeholder='First Name']";
    private static final String LAST_NAME = "input[placeholder='Last Name']";
    private static final String EMAIL = "input[ng-model='EmailAdress']";
    private static final String PHONE = "input[ng-model='Phone']";
    private static final String GENDER = "input[name='radiooptions'][value='Male']";
    private static final String COUNTRY = "#countries";
    private static final String YEAR = "#yearbox";
    private static final String MONTH = "select[ng-model='monthbox']";
    private static final String DAY = "#daybox";
    private static final String PASSWORD = "#firstpassword";
    private static final String CONFIRM_PASSWORD = "#secondpassword";
    public String BuildVersion;

    public boolean hasRequiredFields() {
        return isRequired(FIRST_NAME)
                && isRequired(LAST_NAME)
                && isRequired(EMAIL)
                && isRequired(PHONE)
                && isRequired(GENDER)
                && isRequired(COUNTRY)
                && isRequired(YEAR)
                && isRequired(MONTH)
                && isRequired(DAY)
                && isRequired(PASSWORD)
                && isRequired(CONFIRM_PASSWORD);
    }

    public void version() {
        String version = page.locator("//*[@id='footer']/div/div/div[1]")
                .innerText().toString().trim();

        ConfigReader.set("Version", version);
        ConfigReader.updateExtentProperty("systeminfo.BuildVersion", version);
        System.out.println("Version: " + ConfigReader.get("Version"));
}
    public void enterMismatchedPasswords(String password, String confirmation) {
        fill(PASSWORD, password);
        fill(CONFIRM_PASSWORD, confirmation);
        page.locator(CONFIRM_PASSWORD).press("Tab");
    }

    public String getConfirmPasswordValidationMessage() {
        return (String) page.locator(CONFIRM_PASSWORD).evaluate("element => element.validationMessage");
    }

    private boolean isRequired(String selector) {
        return Boolean.TRUE.equals(page.locator(selector).evaluate("element => element.required"));
    }

}
