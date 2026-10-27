package com.nishitweltech.automation.api;

import com.microsoft.playwright.APIResponse;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.UUID;

public class UserApiTest {
    private static String username() { return "api_" + UUID.randomUUID().toString().replace("-", "").substring(0, 12); }
    private static String user(String username) {
        return "{\"id\":" + Math.abs(UUID.randomUUID().getMostSignificantBits())
                + ",\"username\":\"" + username + "\",\"firstName\":\"Api\",\"lastName\":\"Tester\","
                + "\"email\":\"" + username + "@example.test\",\"password\":\"TestPass123!\",\"phone\":\"1234567890\",\"userStatus\":1}";
    }

    @Test
    public void createGetUpdateAndDeleteUser() {
        String username = username();
        try (ApiClient api = new ApiClient()) {
            APIResponse created = api.request().post("/user", api.json(user(username)));
            Assert.assertEquals(created.status(), 200, created.text());

            APIResponse fetched = api.request().get("/user/" + username);
            Assert.assertEquals(fetched.status(), 200, fetched.text());
            Assert.assertTrue(fetched.text().contains(username));

            APIResponse updated = api.request().put("/user/" + username, api.json(user(username)));
            Assert.assertEquals(updated.status(), 200, updated.text());

            APIResponse deleted = api.request().delete("/user/" + username);
            Assert.assertEquals(deleted.status(), 200, deleted.text());
        }
    }

    @Test
    public void createUsersWithArrayAndList() {
        String first = username();
        String second = username();
        try (ApiClient api = new ApiClient()) {
            String arrayBody = "[" + user(first) + "," + user(second) + "]";
            APIResponse arrayResponse = api.request().post("/user/createWithArray",
                    api.json(arrayBody));
            Assert.assertEquals(arrayResponse.status(), 200, arrayResponse.text());

            APIResponse listResponse = api.request().post("/user/createWithList",
                    api.json("[" + user(username()) + "]"));
            Assert.assertEquals(listResponse.status(), 200, listResponse.text());

            api.request().delete("/user/" + first);
            api.request().delete("/user/" + second);
        }
    }

    @Test
    public void loginAndLogoutUser() {
        String username = username();
        try (ApiClient api = new ApiClient()) {
            APIResponse created = api.request().post("/user", api.json(user(username)));
            Assert.assertEquals(created.status(), 200, created.text());

            APIResponse login = api.request().get("/user/login?username=" + username + "&password=TestPass123%21");
            Assert.assertEquals(login.status(), 200, login.text());
            Assert.assertTrue(login.text().toLowerCase().contains("logged in user session")
                    || login.text().toLowerCase().contains("logged in"), "Unexpected login response: " + login.text());

            APIResponse logout = api.request().get("/user/logout");
            Assert.assertEquals(logout.status(), 200, logout.text());
            api.request().delete("/user/" + username);
        }
    }

    @Test
    public void createUserWithArrayEndpointAcceptsArrayPayload() {
        try (ApiClient api = new ApiClient()) {
            APIResponse response = api.request().post("/user/createWithArray",
                    api.json("[" + user(username()) + "]"));
            Assert.assertEquals(response.status(), 200, response.text());
        }
    }
}
