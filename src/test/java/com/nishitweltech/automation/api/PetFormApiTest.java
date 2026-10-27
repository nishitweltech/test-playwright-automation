package com.nishitweltech.automation.api;

import com.microsoft.playwright.APIResponse;
import com.microsoft.playwright.options.RequestOptions;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.UUID;

public class PetFormApiTest {
    @Test
    public void updatePetNameAndStatusUsingFormEndpoint() {
        long petId = Math.abs(UUID.randomUUID().getMostSignificantBits() >>> 1);
        String body = "{\"id\":" + petId + ",\"name\":\"form-pet\",\"photoUrls\":[\"https://example.test/pet.png\"],\"status\":\"available\"}";
        try (ApiClient api = new ApiClient()) {
            APIResponse create = api.request().post("/pet", api.json(body));
            Assert.assertTrue(create.status() == 200 || create.status() == 201,
                    "Create pet failed: " + create.status() + " " + create.text());

            APIResponse update = api.request().post("/pet/" + petId,
                    RequestOptions.create()
                            .setHeader("Content-Type", "application/x-www-form-urlencoded")
                            .setData("name=form-pet-updated&status=pending"));
            Assert.assertEquals(update.status(), 200, update.text());

            APIResponse get = api.request().get("/pet/" + petId);
            Assert.assertEquals(get.status(), 200, get.text());
            Assert.assertTrue(get.text().contains("form-pet-updated"), get.text());
            api.request().delete("/pet/" + petId);
        }
    }
}
