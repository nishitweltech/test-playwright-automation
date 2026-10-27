package com.nishitweltech.automation.api;

import com.microsoft.playwright.APIRequest;
import com.microsoft.playwright.APIRequestContext;
import com.microsoft.playwright.Playwright;
import com.microsoft.playwright.options.RequestOptions;

import java.util.Map;

/** Shared Playwright API client. Each test owns and closes its own context. */
public final class ApiClient implements AutoCloseable {
    public static final String BASE_URL = System.getProperty(
            "apiBaseUrl",
            System.getenv().getOrDefault("PETSTORE_BASE_URL", "https://petstore.swagger.io/v2")
    );

    private final Playwright playwright;
    private final APIRequestContext request;

    public ApiClient() {
        playwright = Playwright.create();
        request = playwright.request().newContext(new APIRequest.NewContextOptions()
                .setBaseURL(BASE_URL)
                .setExtraHTTPHeaders(Map.of(
                        "Accept", "application/json",
                        "Content-Type", "application/json"
                )));
    }

    public APIRequestContext request() {
        return request;
    }

    public RequestOptions json(String body) {
        return RequestOptions.create().setData(body);
    }

    @Override
    public void close() {
        request.dispose();
        playwright.close();
    }
}
