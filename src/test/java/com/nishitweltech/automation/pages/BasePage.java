package com.nishitweltech.automation.pages;

import com.microsoft.playwright.Page;
import com.nishitweltech.automation.utils.PlaywrightManager;

public abstract class BasePage {
    protected final Page page;

    protected BasePage() {
        this.page = PlaywrightManager.page();
    }

    protected void navigate(String url) {
        page.navigate(url);
    }

    protected void click(String selector) {
        page.locator(selector).click();
    }

    protected void fill(String selector, String value) {
        page.locator(selector).fill(value);
    }

    protected String text(String selector) {
        return page.locator(selector).innerText();
    }
}
