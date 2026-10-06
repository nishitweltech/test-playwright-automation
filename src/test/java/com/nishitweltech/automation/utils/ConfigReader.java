package com.nishitweltech.automation.utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

public final class ConfigReader {
    private static final String CONFIG_RESOURCE = "config/config.properties";
    private static final Properties PROPERTIES = loadProperties();

    private ConfigReader() {
    }

    private static Properties loadProperties() {
        Properties properties = new Properties();

        try (InputStream input = ConfigReader.class.getClassLoader()
                .getResourceAsStream(CONFIG_RESOURCE)) {
            if (input == null) {
                throw new IllegalStateException(
                        "Required configuration resource '" + CONFIG_RESOURCE
                                + "' was not found on the test classpath");
            }
            properties.load(input);
            return properties;
        } catch (IOException e) {
            throw new IllegalStateException(
                    "Unable to load configuration resource '" + CONFIG_RESOURCE + "'", e);
        }
    }

    public static String get(String key) {
        String systemValue = System.getProperty(key);
        String value = systemValue != null ? systemValue : PROPERTIES.getProperty(key);

        if (value == null) {
            throw new IllegalArgumentException(
                    "Missing configuration property: '" + key + "'");
        }

        return value;
    }
    public static void set(String key, String value) {
        if (key == null || key.isBlank()) {
            throw new IllegalArgumentException("Configuration key cannot be null or blank");
        }

        if (value == null) {
            throw new IllegalArgumentException("Configuration value cannot be null");
        }

        PROPERTIES.setProperty(key, value);
        Path path = Paths.get(
                "src",
                "test",
                "resources",
                "config",
                "config.properties"
        );

        try (OutputStream output = Files.newOutputStream(path)) {
            PROPERTIES.store(output, "Updated by Playwright automation");
        } catch (IOException e) {
            throw new IllegalStateException(
                    "Unable to update configuration file: " + path,
                    e
            );
        }
    }
    public static void updateExtentProperty(String key, String value) {
        Path path = Paths.get(
                "src",
                "test",
                "resources",
                "extent.properties"
        );

        Properties properties = new Properties();

        try {
            if (Files.exists(path)) {
                try (InputStream input = Files.newInputStream(path)) {
                    properties.load(input);
                }
            }

            properties.setProperty(key, value);

            try (OutputStream output = Files.newOutputStream(path)) {
                properties.store(output, null);
            }

        } catch (IOException e) {
            throw new RuntimeException(
                    "Unable to update extent.properties", e
            );
        }
    }
    public static boolean getBoolean(String key) {
        return Boolean.parseBoolean(get(key));
    }
}
