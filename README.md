# Playwright Java BDD Automation Framework

This repository contains a runnable UI test framework for the public demo at
`https://demo.automationtesting.in/Index.html`. It uses Java 17, Maven,
Playwright for Java, Cucumber BDD with JUnit 4, Page Object Model, and the
ExtentReports Cucumber 7 adapter.

## Prerequisites

- JDK 17 or newer
- Maven 3.9 or newer
- Internet access for Maven dependencies, Playwright browser installation, and
  the demo application

## Install the browser

From the repository root, install Playwright's Chromium browser:

```bash
mvn exec:java -Dexec.mainClass=com.microsoft.playwright.CLI -Dexec.args="install chromium"
```

On Linux CI, install the browser's operating-system dependencies as well:

```bash
mvn exec:java -Dexec.mainClass=com.microsoft.playwright.CLI -Dexec.args="install --with-deps chromium"
```

## Run tests

Run all Cucumber scenarios:

```bash
mvn clean test
```

Run only smoke scenarios:

```bash
mvn clean test -Dcucumber.filter.tags="@smoke"
```

Run a specific feature:

```bash
mvn clean test -Dcucumber.features=src/test/resources/features/registration.feature
```

Set configuration with JVM system properties (system properties override
`src/test/resources/config/config.properties`):

```bash
mvn clean test -DbaseUrl=https://demo.automationtesting.in/Index.html -Dheadless=false -Dtimeout=30000
```

The sign-in smoke scenario verifies that the site's actual invalid-credentials
message appears. Registration scenarios check the live form's required fields
and client-side password confirmation. No private or pre-registered account is
needed.

## Reports

After a test run:

- Cucumber HTML: `test-output/cucumber/cucumber.html`
- Cucumber JSON: `test-output/cucumber/cucumber.json`
- Extent Spark HTML: `test-output/extent/Spark.html`
- Failure screenshots are attached to the failed Cucumber scenario and included
  in the report output.

## Project structure

```text
src/test/java/com/nishitweltech/automation/
  hooks/          Browser setup, teardown, and failure screenshots
  pages/          LandingPage, SignInPage, RegistrationPage, and BasePage
  runners/        Cucumber JUnit runner and report configuration
  steps/          Sign-in and registration step definitions
  utils/          Configuration reader and Playwright lifecycle
src/test/resources/
  config/         Runtime defaults
  features/       Cucumber scenarios
  extent.properties
  extent-config.xml
```
