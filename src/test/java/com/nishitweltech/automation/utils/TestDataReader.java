package com.nishitweltech.automation.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class TestDataReader {
    private TestDataReader() {
    }

    public static List<Map<String, String>> readCsv(String resourcePath) {
        try (InputStream input = TestDataReader.class.getClassLoader()
                .getResourceAsStream(resourcePath)) {
            if (input == null) {
                throw new IllegalArgumentException(
                        "Test data resource was not found: " + resourcePath);
            }

            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(input, StandardCharsets.UTF_8))) {
                String headerLine = reader.readLine();
                if (headerLine == null || headerLine.isBlank()) {
                    return List.of();
                }

                String[] headers = parseLine(headerLine);
                List<Map<String, String>> rows = new ArrayList<>();
                String line;

                while ((line = reader.readLine()) != null) {
                    if (line.isBlank()) {
                        continue;
                    }

                    String[] values = parseLine(line);
                    Map<String, String> row = new LinkedHashMap<>();
                    for (int i = 0; i < headers.length; i++) {
                        row.put(headers[i],
                                i < values.length ? values[i] : "");
                    }
                    rows.add(row);
                }
                return rows;
            }
        } catch (IOException e) {
            throw new IllegalStateException(
                    "Unable to read test data resource: " + resourcePath, e);
        }
    }

    private static String[] parseLine(String line) {
        List<String> values = new ArrayList<>();
        StringBuilder value = new StringBuilder();
        boolean quoted = false;

        for (int i = 0; i < line.length(); i++) {
            char current = line.charAt(i);

            if (current == '"') {
                if (quoted && i + 1 < line.length() && line.charAt(i + 1) == '"') {
                    value.append('"');
                    i++;
                } else {
                    quoted = !quoted;
                }
            } else if (current == ',' && !quoted) {
                values.add(value.toString().trim());
                value.setLength(0);
            } else {
                value.append(current);
            }
        }

        values.add(value.toString().trim());
        return values.toArray(String[]::new);
    }
}
