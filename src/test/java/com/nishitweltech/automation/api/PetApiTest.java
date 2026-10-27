package com.nishitweltech.automation.api;

import com.microsoft.playwright.APIResponse;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.UUID;

public class PetApiTest {
    private static long id() { return Math.abs(UUID.randomUUID().getMostSignificantBits()); }
    private static String pet(long id, String name, String status) {
        return "{\"id\":" + id + ",\"name\":\"" + name + "\",\"photoUrls\":[\"https://example.test/pet.png\"],"
                + "\"status\":\"" + status + "\",\"category\":{\"id\":1,\"name\":\"test\"},"
                + "\"tags\":[{\"id\":1,\"name\":\"api-test\"}]}";
    }

    @Test
    public void addGetUpdateAndDeletePet() {
        long petId = id();
        try (ApiClient api = new ApiClient()) {
            APIResponse created = api.request().post("/pet", api.json(pet(petId, "api-pet", "available")));
            Assert.assertTrue(created.status() == 200 || created.status() == 201, "Create pet status: " + created.status());
            Assert.assertTrue(created.text().contains("api-pet"));

            APIResponse fetched = api.request().get("/pet/" + petId);
            Assert.assertEquals(fetched.status(), 200, fetched.text());
            Assert.assertTrue(fetched.text().contains("\"id\":" + petId));

            APIResponse updated = api.request().put("/pet", api.json(pet(petId, "api-pet-updated", "sold")));
            Assert.assertEquals(updated.status(), 200, updated.text());
            Assert.assertTrue(updated.text().contains("api-pet-updated"));

            APIResponse deleted = api.request().delete("/pet/" + petId);
            Assert.assertEquals(deleted.status(), 200, deleted.text());
        }
    }

    @Test
    public void findPetsByEachSupportedStatus() {
        try (ApiClient api = new ApiClient()) {
            for (String status : new String[]{"available", "pending", "sold"}) {
                APIResponse response = api.request().get("/pet/findByStatus?status=" + status);
                Assert.assertEquals(response.status(), 200, "Status filter " + status + ": " + response.text());
                Assert.assertTrue(response.headers().getOrDefault("content-type", "").contains("application/json")
                        || response.text().startsWith("[") || response.text().equals("null"),
                        "Expected JSON response for status " + status);
            }
        }
    }

    @Test
    public void findPetsByTagsAndValidateMissingPet() {
        try (ApiClient api = new ApiClient()) {
            APIResponse byTag = api.request().get("/pet/findByTags?tags=api-test");
            Assert.assertEquals(byTag.status(), 200, byTag.text());

            APIResponse missing = api.request().get("/pet/9223372036854775000");
            Assert.assertTrue(missing.status() == 404 || missing.status() == 400,
                    "Expected client error for unknown pet; got " + missing.status() + ": " + missing.text());
        }
    }

    @Test
    public void uploadPetImageEndpointAcceptsPetId() {
        long petId = id();
        try (ApiClient api = new ApiClient()) {
            api.request().post("/pet", api.json(pet(petId, "image-test-pet", "available")));
            APIResponse response = api.request().post("/pet/" + petId + "/uploadImage",
                    com.microsoft.playwright.options.RequestOptions.create()
                            .setHeader("Content-Type", "application/octet-stream")
                            .setData("sample image payload"));
            Assert.assertTrue(response.status() == 200 || response.status() == 201,
                    "Upload image status: " + response.status() + " " + response.text());
            api.request().delete("/pet/" + petId);
        }
    }
}
