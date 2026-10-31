package com.nishitweltech.automation.cli;

import jakarta.activation.DataHandler;
import jakarta.activation.FileDataSource;
import jakarta.mail.*;
import jakarta.mail.internet.*;

import java.io.Console;
import java.io.IOException;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Stream;

/**
 * Interactive local runner for Maven + Cucumber/TestNG with optional report email.
 * SMTP credentials are read only from environment variables.
 */
public final class TestExecutionAgent {
    private static final Path ROOT = Path.of("").toAbsolutePath().normalize();
    private static final Path SCRIPTS = ROOT.resolve("scripts");
    private static final List<Path> REPORTS = List.of(
            ROOT.resolve("test-output/extent/Spark.html"),
            ROOT.resolve("test-output/cucumber/cucumber.html"),
            ROOT.resolve("test-output/cucumber/cucumber.json")
    );

    private TestExecutionAgent() { }

    public static void main(String[] args) throws Exception {
        Scanner scanner = new Scanner(System.in);
        boolean emailFlag = Arrays.asList(args).contains("--email");
        boolean noEmailFlag = Arrays.asList(args).contains("--no-email");

        System.out.println("\n==========================================");
        System.out.println(" Playwright Maven Test Execution Agent");
        System.out.println("==========================================");

        String baseUrl = ask(scanner, "Base URL",
                "https://demo.automationtesting.in/Index.html");
        String headless = askChoice(scanner, "Run browser headless?", "yes", "yes", "no");
        String tags = ask(scanner, "Cucumber tags (blank = all)", "@smoke");
        String feature = ask(scanner, "Feature file (blank = all features)", "");
        Path script = chooseScript(scanner);

        if (script != null) {
            int scriptExit = runScript(script);
            if (scriptExit != 0) {
                System.err.println("Pre-test script failed with exit code " + scriptExit
                        + ". Maven tests were not started.");
                System.exit(scriptExit);
            }
        }

        List<String> command = new ArrayList<>();
        command.add(mavenCommand());
        command.add("clean");
        command.add("test");
        command.add("-DbaseUrl=" + baseUrl);
        command.add("-Dheadless=" + headless.equalsIgnoreCase("yes"));
        if (!tags.isBlank()) command.add("-Dcucumber.filter.tags=" + tags);
        if (!feature.isBlank()) command.add("-Dcucumber.features=" + feature);

        System.out.println("\nExecuting: " + String.join(" ", command));
        LocalDateTime started = LocalDateTime.now();
        int testExit = new ProcessBuilder(command).directory(ROOT.toFile())
                .inheritIO().start().waitFor();
        LocalDateTime finished = LocalDateTime.now();

        System.out.println("\nTest execution " + (testExit == 0 ? "PASSED" : "FAILED")
                + " (exit code " + testExit + ").");
        System.out.println("Reports are collected from test-output/.");

        boolean sendEmail = emailFlag || (!noEmailFlag
                && askChoice(scanner, "Email reports to customer?", "no", "yes", "no")
                .equalsIgnoreCase("yes"));

        if (sendEmail) {
            String recipients = ask(scanner, "Customer email(s), comma-separated",
                    System.getenv().getOrDefault("REPORT_EMAIL_TO", ""));
            sendReports(recipients, testExit, started, finished);
        }

        System.exit(testExit);
    }

    private static String ask(Scanner scanner, String label, String defaultValue) {
        System.out.print(label + (defaultValue.isBlank() ? "" : " [" + defaultValue + "]")
                + ": ");
        String value = scanner.nextLine().trim();
        return value.isBlank() ? defaultValue : value;
    }

    private static String askChoice(Scanner scanner, String label, String defaultValue,
                                    String... allowed) {
        while (true) {
            String value = ask(scanner, label, defaultValue).toLowerCase(Locale.ROOT);
            if (Arrays.asList(allowed).contains(value)) return value;
            System.out.println("Choose one of: " + String.join(", ", allowed));
        }
    }

    private static Path chooseScript(Scanner scanner) throws IOException {
        if (!Files.isDirectory(SCRIPTS)) {
            System.out.println("No scripts/ directory found; skipping pre-test script.");
            return null;
        }
        List<Path> available;
        try (Stream<Path> stream = Files.list(SCRIPTS)) {
            available = stream.filter(Files::isRegularFile)
                    .filter(p -> {
                        String n = p.getFileName().toString().toLowerCase(Locale.ROOT);
                        return n.endsWith(".sh") || n.endsWith(".bat") || n.endsWith(".cmd")
                                || n.endsWith(".ps1");
                    }).sorted().toList();
        }
        if (available.isEmpty()) {
            System.out.println("No .sh/.bat/.cmd/.ps1 scripts found; skipping.");
            return null;
        }
        System.out.println("\nPre-test scripts:");
        for (int i = 0; i < available.size(); i++)
            System.out.printf("  %d) %s%n", i + 1, available.get(i).getFileName());
        System.out.println("  0) Skip");
        String selection = ask(scanner, "Select script", "0");
        int index;
        try { index = Integer.parseInt(selection); }
        catch (NumberFormatException e) { throw new IllegalArgumentException("Invalid script selection"); }
        if (index == 0) return null;
        if (index < 1 || index > available.size())
            throw new IllegalArgumentException("Script selection is out of range");
        return available.get(index - 1).normalize();
    }

    private static int runScript(Path script) throws IOException, InterruptedException {
        String name = script.getFileName().toString().toLowerCase(Locale.ROOT);
        List<String> command;
        if (name.endsWith(".sh")) command = List.of("bash", script.toString());
        else if (name.endsWith(".ps1")) command = List.of("powershell", "-ExecutionPolicy",
                "Bypass", "-File", script.toString());
        else command = List.of("cmd", "/c", script.toString());
        System.out.println("\nRunning pre-test script: " + script);
        return new ProcessBuilder(command).directory(ROOT.toFile()).inheritIO().start().waitFor();
    }

    private static String mavenCommand() {
        Path wrapper = ROOT.resolve(isWindows() ? "mvnw.cmd" : "mvnw");
        return Files.isRegularFile(wrapper) ? wrapper.toString() : "mvn";
    }

    private static boolean isWindows() {
        return System.getProperty("os.name").toLowerCase(Locale.ROOT).contains("win");
    }

    private static void sendReports(String recipients, int testExit,
                                    LocalDateTime started, LocalDateTime finished)
            throws MessagingException {
        if (recipients == null || recipients.isBlank())
            throw new IllegalArgumentException("At least one customer email address is required.");

        String host = requiredEnv("SMTP_HOST");
        String from = requiredEnv("SMTP_FROM");
        String port = System.getenv().getOrDefault("SMTP_PORT", "587");
        String username = System.getenv("SMTP_USERNAME");
        String password = System.getenv("SMTP_PASSWORD");
        boolean auth = username != null && !username.isBlank();

        Properties props = new Properties();
        props.put("mail.smtp.host", host);
        props.put("mail.smtp.port", port);
        props.put("mail.smtp.auth", Boolean.toString(auth));
        props.put("mail.smtp.starttls.enable",
                System.getenv().getOrDefault("SMTP_STARTTLS", "true"));
        Session session = Session.getInstance(props, auth ? new Authenticator() {
            @Override protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password == null ? "" : password);
            }
        } : null);

        MimeMessage message = new MimeMessage(session);
        message.setFrom(new InternetAddress(from));
        message.setRecipients(Message.RecipientType.TO,
                InternetAddress.parse(recipients.replace(';', ',')));
        message.setSubject("Automation Test Report - "
                + (testExit == 0 ? "PASSED" : "FAILED"));
        MimeBodyPart body = new MimeBodyPart();
        body.setText("Automation execution: " + (testExit == 0 ? "PASSED" : "FAILED")
                + "\nStarted: " + started.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
                + "\nFinished: " + finished.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
                + "\nExit code: " + testExit
                + "\n\nPlease find the available test reports attached.", "UTF-8");

        Multipart multipart = new MimeMultipart();
        multipart.addBodyPart(body);
        int attached = 0;
        for (Path report : REPORTS) {
            if (Files.isRegularFile(report)) {
                MimeBodyPart attachment = new MimeBodyPart();
                attachment.setDataHandler(new DataHandler(new FileDataSource(report.toFile())));
                attachment.setFileName(report.getFileName().toString());
                multipart.addBodyPart(attachment);
                attached++;
            }
        }
        if (attached == 0)
            throw new MessagingException("No report files found under test-output; email not sent.");
        message.setContent(multipart);
        Transport.send(message);
        System.out.println("Email sent with " + attached + " report attachment(s).");
    }

    private static String requiredEnv(String key) {
        String value = System.getenv(key);
        if (value == null || value.isBlank())
            throw new IllegalStateException("Missing required environment variable: " + key);
        return value;
    }
}
