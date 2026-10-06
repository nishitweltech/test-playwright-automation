package com.nishitweltech.automation.utils;

import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.List;
import java.util.Map;

public class TestDataReaderTest {
    @Test
    public void shouldReadLoginDataFromCsv() {
        List<Map<String, String>> data =
                TestDataReader.readCsv("testdata/login-data.csv");

        Assert.assertFalse(data.isEmpty(), "Expected login test data");
        Assert.assertEquals(data.get(0).get("username"), "invalid@example.com");
        Assert.assertEquals(data.get(0).get("password"), "wrong-password");
        Assert.assertEquals(
                data.get(0).get("expectedMessage"),
                "Invalid username or password");
    }
}
