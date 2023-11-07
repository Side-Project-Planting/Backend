package com.example.planservice.presentation;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

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
import com.example.planservice.presentation.dto.response.LabelFindResponse;
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
    @DisplayName("라벨을 생성할 때 이름을 입력해야만 한다")
    void testCreateLabelFailEmptyName() throws Exception {
        // given
        Long userId = 1L;
        Long createdLabelId = 2L;
        Long planId = 3L;

        LabelCreateRequest request = LabelCreateRequest.builder()
            .name("")
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
            .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("라벨을 생성할 때 플랜의 ID를 입력해야만 한다")
    void testCreateLabelFailEmptyPlanId() throws Exception {
        // given
        String labelName = "이름";
        Long userId = 1L;
        Long createdLabelId = 2L;

        LabelCreateRequest request = LabelCreateRequest.builder()
            .name(labelName)
            .planId(null)
            .build();

        // stub
        when(labelService.create(anyLong(), any(LabelCreateRequest.class)))
            .thenReturn(createdLabelId);

        // when & then
        mockMvc.perform(post("/labels")
                .header("X-User-Id", userId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest());
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

    @Test
    @DisplayName("라벨을 삭제하면 204 상태를 반환한다")
    void testDeleteLabel() throws Exception {
        // given
        Long userId = 1L;
        Long labelId = 2L;
        Long planId = 3L;

        // when & then
        mockMvc.perform(delete("/labels/" + labelId + "?planId=" + planId)
                .header("X-User-Id", userId)
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("로그인하지 않은 사용자가 라벨을 삭제하려 하면 401 상태를 반환한다")
    void testDeleteLabelFailNotLogin() throws Exception {
        // given
        Long labelId = 2L;
        Long planId = 3L;

        // when & then
        mockMvc.perform(delete("/labels/" + labelId + "?planId=" + planId)
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("라벨을 삭제하려면 해당 라벨이 속한 Plan의 ID를 함께 입력해야 한다")
    void testDeleteLabelFailInvalidId() throws Exception {
        // given
        Long userId = 1L;
        Long labelId = 2L;

        // when & then
        mockMvc.perform(delete("/labels/" + labelId)
                .header("X-User-Id", userId)
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("라벨을 조회한다")
    void testFindLabel() throws Exception {
        // given
        Long targetLabelId = 1L;
        Long userId = 2L;

        LabelFindResponse response = LabelFindResponse.builder()
            .id(targetLabelId)
            .planId(3L)
            .name("반환된 라벨명")
            .build();

        // stub
        when(labelService.find(targetLabelId, userId))
            .thenReturn(response);

        // when & then
        mockMvc.perform(get("/labels/" + targetLabelId)
                .header("X-User-Id", userId))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(response.getId()))
            .andExpect(jsonPath("$.name").value(response.getName()))
            .andExpect(jsonPath("$.planId").value(response.getPlanId()));
    }

    @Test
    @DisplayName("로그인한 사용자만 탭을 조회할 수 있다")
    void testFindTabFailNotLogin() throws Exception {
        // given
        Long targetLabelId = 1L;

        // when & then
        mockMvc.perform(get("/labels/" + targetLabelId))
            .andExpect(status().isUnauthorized());
    }

}