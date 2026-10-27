package com.nishitweltech.automation.api;

import com.microsoft.playwright.APIResponse;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.UUID;

public class StoreApiTest {
    private static long id() { return Math.abs(UUID.randomUUID().getMostSignificantBits()); }
    private static String order(long id) {
        return "{\"id\":" + id + ",\"petId\":1,\"quantity\":2,\"shipDate\":\"2026-01-01T00:00:00.000Z\","
                + "\"status\":\"placed\",\"complete\":true}";
    }

    @Test
    public void inventoryEndpointReturnsStatusCounts() {
        try (ApiClient api = new ApiClient()) {
            APIResponse response = api.request().get("/store/inventory");
            Assert.assertEquals(response.status(), 200, response.text());
            Assert.assertTrue(response.text().startsWith("{"), "Inventory should be a JSON object");
        }
    }

    @Test
    public void createReadAndDeleteOrder() {
        long orderId = id();
        try (ApiClient api = new ApiClient()) {
            APIResponse created = api.request().post("/store/order", api.json(order(orderId)));
            Assert.assertTrue(created.status() == 200 || created.status() == 201,
                    "Create order status: " + created.status() + " " + created.text());

            APIResponse fetched = api.request().get("/store/order/" + orderId);
            Assert.assertEquals(fetched.status(), 200, fetched.text());
            Assert.assertTrue(fetched.text().contains("\"id\":" + orderId));

            APIResponse deleted = api.request().delete("/store/order/" + orderId);
            Assert.assertEquals(deleted.status(), 200, deleted.text());
        }
    }

    @Test
    public void invalidOrderIdReturnsClientError() {
        try (ApiClient api = new ApiClient()) {
            APIResponse response = api.request().get("/store/order/0");
            Assert.assertTrue(response.status() == 400 || response.status() == 404,
                    "Expected 400/404 for invalid order ID, got " + response.status() + ": " + response.text());
        }
    }
}
