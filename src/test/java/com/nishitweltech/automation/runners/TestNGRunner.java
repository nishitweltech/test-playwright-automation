package com.nishitweltech.automation.runners;

import io.cucumber.testng.AbstractTestNGCucumberTests;
import io.cucumber.testng.CucumberOptions;

@CucumberOptions(
        features = "src/test/resources/features",
        glue = "com.nishitweltech.automation",
        plugin = {
                "pretty",
                "html:test-output/cucumber/cucumber.html",
                "json:test-output/cucumber/cucumber.json",
                "com.aventstack.extentreports.cucumber.adapter.ExtentCucumberAdapter:"
        },
        monochrome = true
)
public class TestNGRunner extends AbstractTestNGCucumberTests {
}
