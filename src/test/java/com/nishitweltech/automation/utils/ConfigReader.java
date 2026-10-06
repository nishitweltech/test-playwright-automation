package com.nishitweltech.automation.utils;

import java.io.IOException;
import java.io.InputStream;
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

    public static boolean getBoolean(String key) {
        return Boolean.parseBoolean(get(key));
    }
}
