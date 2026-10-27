package com.nishitweltech.automation.api;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

import java.nio.file.Files;
import java.nio.file.Path;

public class ApiTestListener implements ITestListener {
    private static ExtentReports extent;
    private static final ThreadLocal<ExtentTest> CURRENT = new ThreadLocal<>();

    @Override
    public void onStart(ITestContext context) {
        try {
            Files.createDirectories(Path.of("test-output", "api"));
            ExtentSparkReporter reporter = new ExtentSparkReporter("test-output/api/ApiSpark.html");
            reporter.config().setDocumentTitle("Petstore API Test Report");
            reporter.config().setReportName("Playwright Java API Tests");
            extent = new ExtentReports();
            extent.attachReporter(reporter);
            extent.setSystemInfo("API Base URL", ApiClient.BASE_URL);
            extent.setSystemInfo("Framework", "Playwright Java + TestNG");
        } catch (Exception e) {
            throw new IllegalStateException("Could not initialize API Extent report", e);
        }
    }

    @Override public void onTestStart(ITestResult result) {
        CURRENT.set(extent.createTest(result.getMethod().getMethodName()));
    }
    @Override public void onTestSuccess(ITestResult result) {
        CURRENT.get().pass("Passed");
        CURRENT.remove();
    }
    @Override public void onTestFailure(ITestResult result) {
        CURRENT.get().fail(result.getThrowable());
        CURRENT.remove();
    }
    @Override public void onTestSkipped(ITestResult result) {
        CURRENT.get().skip("Skipped");
        CURRENT.remove();
    }
    @Override public void onFinish(ITestContext context) {
        if (extent != null) extent.flush();
    }
}
