package com.example.planservice.presentation;


import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.example.planservice.application.TabService;
import com.example.planservice.presentation.dto.request.TabCreateRequest;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest
class TabControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    TabService tabService;

    @Test
    @DisplayName("Tab을 생성한다")
    void createTab() throws Exception {
        // given
        Long userId = 1L;
        TabCreateRequest request = TabCreateRequest.builder()
            .name("탭이름")
            .build();

        // when & then
        mockMvc.perform(post("/tabs")
                .header("X-User-Id", userId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated())
            .andExpect(header().exists("Location"))
            .andExpect(redirectedUrlPattern("/tabs/*"));
    }
}