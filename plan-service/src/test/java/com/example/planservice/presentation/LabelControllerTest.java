package com.example.planservice.presentation;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import com.example.planservice.application.LabelService;
import com.example.planservice.presentation.dto.request.LabelCreateRequest;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(controllers = LabelController.class)
@ActiveProfiles("test")
class LabelControllerTest {
    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    LabelService labelService;

    @Test
    @DisplayName("라벨을 생성하면 201 상태를 반환한다")
    void testCreateLabel() throws Exception {
        // given
        Long userId = 1L;
        Long createdLabelId = 2L;
        Long planId = 3L;
        String labelName = "라벨명";
        LabelCreateRequest request = LabelCreateRequest.builder()
            .name(labelName)
            .planId(planId)
            .build();

        // stub
        when(labelService.create(anyLong(), any(LabelCreateRequest.class)))
            .thenReturn(createdLabelId);

        // when & then
        mockMvc.perform(post("/labels")
                .header("X-User-Id", userId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated())
            .andExpect(header().string("Location", "/labels/" + createdLabelId));
    }

    @Test
    @DisplayName("로그인하지 않은 사용자가 라벨을 생성하면 401 상태를 반환한다")
    void testCreateLabelFailNotLogin() throws Exception {
        // given
        Long planId = 3L;
        String labelName = "라벨명";
        LabelCreateRequest request = LabelCreateRequest.builder()
            .name(labelName)
            .planId(planId)
            .build();

        // when & then
        mockMvc.perform(post("/labels")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isUnauthorized());
    }
}