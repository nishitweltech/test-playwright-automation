package com.nishitweltech.automation.utils;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;

public final class PlaywrightManager {
    private static final ThreadLocal<Playwright> PLAYWRIGHT = new ThreadLocal<>();
    private static final ThreadLocal<Browser> BROWSER = new ThreadLocal<>();
    private static final ThreadLocal<Page> PAGE = new ThreadLocal<>();

    private PlaywrightManager() {
    }

    public static void start() {
        Playwright playwright = Playwright.create();
        BrowserType.LaunchOptions options = new BrowserType.LaunchOptions()
                .setHeadless(ConfigReader.getBoolean("headless"));
        Browser browser = playwright.chromium().launch(options);
        Page page = browser.newPage(new Browser.NewPageOptions()
                .setViewportSize(1440, 900));
        page.setDefaultTimeout(Long.parseLong(ConfigReader.get("timeout")));
        PLAYWRIGHT.set(playwright);
        BROWSER.set(browser);
        PAGE.set(page);


    }

    public static Page page() {
        Page page = PAGE.get();
        if (page == null) {
            throw new IllegalStateException("Playwright has not been started for this thread");
        }
        return page;
    }

    public static void stop() {
        try {
            if (PAGE.get() != null) {
                PAGE.get().close();
            }
            if (BROWSER.get() != null) {
                BROWSER.get().close();
            }
            if (PLAYWRIGHT.get() != null) {
                PLAYWRIGHT.get().close();
            }
        } finally {
            PAGE.remove();
            BROWSER.remove();
            PLAYWRIGHT.remove();
        }
    }
}
