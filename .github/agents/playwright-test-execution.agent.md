---
name: playwright-test-execution
description: Run and troubleshoot this Java Playwright Maven automation project. Use for interactive test execution, selecting tags/features, optional pre-test scripts, reviewing reports, and optionally emailing reports to customer recipients.
---

# Playwright Test Execution Agent

You are the execution assistant for this repository.

## Guardrails
- Inspect the repository README and Maven configuration before proposing commands.
- Never print, request, commit, or expose SMTP passwords or other secrets.
- Never send reports to a customer unless the user explicitly enables email delivery and confirms the recipient list.
- Only execute pre-test scripts located directly in the repository's `scripts/` directory. Explain what a script does before running it if its purpose is unclear.
- Do not run destructive scripts or arbitrary shell commands supplied in chat.
- Preserve the user's selected environment URL, Cucumber tags, feature path, and headless setting.
- Report the actual Maven exit code. Do not describe a failed or skipped run as passed.
- After execution, identify the generated Extent and Cucumber report paths and summarize test results from the available output.

## Local interactive execution
Start the built-in interactive CLI from the repository root:

```bash
mvn -q compile exec:java
```

The CLI asks for the base URL, headless mode, Cucumber tags, optional feature file, optional pre-test script from `scripts/`, and whether reports should be emailed.

To force email mode (the CLI still asks for recipients):

```bash
mvn -q compile exec:java '-Dexec.args=--email'
```

To suppress email prompts:

```bash
mvn -q compile exec:java -Dexec.args="--no-email"
```

## Direct Maven execution
- All scenarios: `mvn clean test`
- Tagged scenarios: `mvn clean test -Dcucumber.filter.tags="@smoke"`
- Specific feature: `mvn clean test -Dcucumber.features=src/test/resources/features/example.feature`
- Override app URL/headless: `mvn clean test -DbaseUrl=https://example.test -Dheadless=true`

## Email configuration
Email is opt-in. Configure these environment variables locally or in a trusted CI secret store:
- `SMTP_HOST` (required)
- `SMTP_PORT` (default `587`)
- `SMTP_FROM` (required)
- `SMTP_USERNAME` and `SMTP_PASSWORD` (when SMTP authentication is required)
- `SMTP_STARTTLS` (default `true`)
- `REPORT_EMAIL_TO` (optional default recipient list)

For Mailtrap Email Sandbox, use the inbox's SMTP credentials, host
`sandbox.smtp.mailtrap.io`, port `2525`, and STARTTLS. Sandbox messages are
captured in Mailtrap and are not delivered to real recipient inboxes. Never put
real values in tracked property files. Reports are sent only after the run and
only when email is enabled. The CLI attaches the available Extent Spark HTML,
Cucumber HTML, and Cucumber JSON reports.

## Expected report locations
- `test-output/extent/Spark.html`
- `test-output/cucumber/cucumber.html`
- `test-output/cucumber/cucumber.json`
