# Playwright Java BDD Automation Framework

A maintainable UI automation framework built with:

- Java 17
- Maven
- Microsoft Playwright
- Cucumber BDD
- Page Object Model
- Extent Reports
- JUnit 4

## Project structure

```
src/
  test/
    java/com/nishitweltech/automation/
      hooks/
        TestHooks.java
      pages/
        BasePage.java
        LoginPage.java
      runners/
        TestRunner.java
      steps/
        LoginSteps.java
      utils/
        ConfigReader.java
        PlaywrightManager.java
    resources/
      config/
        config.properties
      features/
        login.feature
      extent.properties
```

## Prerequisites

- JDK 17+
- Maven 3.9+
- Internet access for Maven dependencies and the demo application

## Run tests

Install Playwright browser binaries:

```bash
mvn exec:java -Dexec.mainClass=com.microsoft.playwright.CLI -Dexec.args="install chromium"
```

Run the BDD suite:

```bash
mvn clean test
```

Run against another URL:

```bash
mvn clean test -DbaseUrl=https://your-app.example.com
```

Reports are generated under `test-output/extent/`.

The sample feature targets Sauce Demo and uses its public standard test credentials. Replace them in `config.properties` or pass JVM properties for your own application.

## Design notes

- `PlaywrightManager` owns the Playwright lifecycle.
- `BasePage` centralizes common page operations.
- Page classes contain locators and UI actions only.
- Cucumber step definitions contain business-level glue, not locator details.
- Hooks capture a screenshot on failure and attach it to the Cucumber scenario.
- Extent's Cucumber 7 adapter produces the HTML report.
