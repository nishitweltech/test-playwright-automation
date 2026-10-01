package com.nishitweltech.automation.hooks;

import com.nishitweltech.automation.utils.PlaywrightManager;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;

import java.nio.file.Files;
import java.nio.file.Path;

public class TestHooks {
    @Before
    public void setUp() {
        PlaywrightManager.start();
    }

    @After
    public void tearDown(Scenario scenario) {
        try {
            if (scenario.isFailed()) {
                byte[] screenshot = PlaywrightManager.page().screenshot(
                        new com.microsoft.playwright.Page.ScreenshotOptions().setFullPage(true));
                scenario.attach(screenshot, "image/png", "Failure screenshot");
            }
        } finally {
            PlaywrightManager.stop();
        }
    }
}
