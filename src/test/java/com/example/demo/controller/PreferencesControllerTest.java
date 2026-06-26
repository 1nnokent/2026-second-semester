package com.example.demo.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.cookie;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
class PreferencesControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void shouldReturnDefaultViewPreferenceAndSetCookie() throws Exception {
        mockMvc.perform(get("/api/preferences/view"))
                .andExpect(status().isOk())
                .andExpect(cookie().value("viewPreference", "detailed"))
                .andExpect(jsonPath("$.mode").value("detailed"));
    }

    @Test
    void shouldUpdateViewPreferenceCookie() throws Exception {
        mockMvc.perform(post("/api/preferences/view").param("mode", "compact"))
                .andExpect(status().isOk())
                .andExpect(cookie().value("viewPreference", "compact"))
                .andExpect(jsonPath("$.mode").value("compact"));
    }

    @Test
    void shouldReturnBadRequestForInvalidViewPreference() throws Exception {
        mockMvc.perform(post("/api/preferences/view").param("mode", "wide"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(containsString(
                        "mode must be either compact or detailed")));
    }

    @Test
    void shouldReturnBadRequestWhenModeParameterIsMissing() throws Exception {
        mockMvc.perform(post("/api/preferences/view"))
                .andExpect(status().isBadRequest());
    }
}
