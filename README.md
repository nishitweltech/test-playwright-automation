# Playwright Java BDD Automation Framework

This repository contains a runnable UI test framework for the public demo at
`https://demo.automationtesting.in/Index.html`. It uses Java 17, Maven,
Playwright for Java, Cucumber BDD with TestNG, Page Object Model, and the
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

## Interactive local execution agent

Launch the user-interactive CLI from the repository root:

```bash
mvn -q compile exec:java
```

The CLI prompts for the application base URL, headless mode, Cucumber tag
expression, optional feature file, optional pre-test script from `scripts/`,
and whether to email generated reports.

Email can be explicitly enabled or disabled:

```bash
mvn -q compile exec:java -Dexec.args="--email"
mvn -q compile exec:java -Dexec.args="--no-email"
```

The CLI runs `mvn clean test`, preserves the test exit code, and can attach the
Extent Spark HTML, Cucumber HTML, and Cucumber JSON reports. Email is opt-in.

### SMTP environment configuration

Configure these environment variables outside source control. For Mailtrap
Email Sandbox, use the SMTP credentials shown in your Mailtrap inbox:

| Variable | Purpose |
|---|---|
| `SMTP_HOST` | Mailtrap sandbox host, usually `sandbox.smtp.mailtrap.io` |
| `SMTP_PORT` | Mailtrap sandbox port, usually `2525` |
| `SMTP_FROM` | Sender address (required; e.g. `automation@example.com`) |
| `SMTP_USERNAME` | Mailtrap sandbox SMTP username |
| `SMTP_PASSWORD` | Mailtrap sandbox SMTP password |
| `SMTP_STARTTLS` | Enable required STARTTLS (default: `true`) |
| `REPORT_EMAIL_TO` | Default recipient(s), comma-separated |

Mailtrap Email Sandbox captures messages in its inbox for testing; it does not
deliver them to the recipient's real email inbox.

After configuring the SMTP values in your Windows user environment or secret
store, run the CLI from a new PowerShell window:

```powershell
mvn -q compile exec:java '-Dexec.args=--email'
```

Keep SMTP credentials in user-level environment variables or a trusted secret
store, never in `.env.example` or other tracked files. Restart IntelliJ and
open a new terminal after changing persistent Windows environment variables.
The checked-in `.env.example` contains placeholders and is not loaded by the
CLI; the CLI reads process environment variables.

### Pre-test scripts

Put reviewed setup scripts directly inside `scripts/`. Supported extensions:
`.sh`, `.bat`, `.cmd`, and `.ps1`. The CLI displays available scripts and
asks before executing one. Select `0` to skip. Do not put secrets in scripts.

The GitHub Copilot custom agent is available at
`.github/agents/playwright-test-execution.agent.md`.

## Direct Maven execution

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

Override the application URL/headless setting:

```bash
mvn clean test -DbaseUrl=https://demo.automationtesting.in/Index.html -Dheadless=false -Dtimeout=30000
```

## TestNG, Extent Reports, and Test Data

- TestNG runner: `src/test/java/com/nishitweltech/automation/runners/TestNGRunner.java`
- TestNG suite: `src/test/resources/testng.xml`
- Extent configuration: `src/test/resources/extent.properties` and `src/test/resources/extent-config.xml`
- Extent Spark report: `test-output/extent/Spark.html`
- Reusable CSV reader: `src/test/java/com/nishitweltech/automation/utils/TestDataReader.java`
- Sample data: `src/test/resources/testdata/login-data.csv`

## Reports

- Cucumber HTML: `test-output/cucumber/cucumber.html`
- Cucumber JSON: `test-output/cucumber/cucumber.json`
- Extent Spark HTML: `test-output/extent/Spark.html`
- Failure screenshots: `test-output/extent/screenshots/`
