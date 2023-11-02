package com.example.planservice.presentation;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.example.planservice.application.PlanService;
import com.example.planservice.exception.ApiException;
import com.example.planservice.exception.ErrorCode;
import com.example.planservice.presentation.dto.request.PlanCreateRequest;
import com.example.planservice.presentation.dto.response.PlanResponse;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(PlanController.class)
class PlanControllerTest {
    @MockBean
    PlanService planService;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("플랜을 생성한다")
    void createPlan() throws Exception {
        // given
        Long userId = 1L;
        List<String> invitedEmails = List.of("test@example.com");
        PlanCreateRequest request = PlanCreateRequest.builder()
            .title("플랜 제목")
            .intro("플랜 소개")
            .isPublic(true)
            .invitedEmails(invitedEmails)
            .build();

        when(planService.create(any(PlanCreateRequest.class), anyLong()))
            .thenReturn(1L);

        // when & then
        mockMvc.perform(post("/plans")
                .header("X-User-Id", userId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated())
            .andExpect(header().exists("Location"))
            .andExpect(redirectedUrlPattern("/plans/*"));
    }

    @Test
    @DisplayName("로그인하지 않은 사용자는 플랜을 생성할 수 없다")
    void createPlanFailNotLogin() throws Exception {
        // given
        List<String> invitedEmails = new ArrayList<>();
        invitedEmails.add("A@gmail.com");
        invitedEmails.add("B@gmail.com");

        PlanCreateRequest request = PlanCreateRequest.builder()
            .title("플랜 제목")
            .intro("플랜 소개")
            .isPublic(true)
            .invitedEmails(invitedEmails)
            .build();

        // when & then
        mockMvc.perform(post("/plans")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("유효하지 않은 이메일로 플랜을 생성하려고 하면 실패한다")
    void createPlanFailInvalidEmail() throws Exception {
        // given
        Long userId = 1L;
        List<String> invalidEmails = List.of("invalid-email");

        PlanCreateRequest request = PlanCreateRequest.builder()
            .title("플랜 제목")
            .intro("플랜 소개")
            .isPublic(true)
            .invitedEmails(invalidEmails)
            .build();

        // when & then
        mockMvc.perform(post("/plans")
                .header("X-User-Id", userId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("플랜 정보를 조회한다")
    void readPlan() throws Exception {
        // given
        Long userId = 1L;
        Long planId = 1L;

        PlanResponse planResponse = PlanResponse.builder()
            .title("플랜 제목")
            .description("플랜 소개")
            .isPublic(true)
            .build();

        when(planService.getTotalPlanResponse(planId)).thenReturn(planResponse);

        // when & then
        mockMvc.perform(get("/plans/{planId}", planId)
                .header("X-User-Id", userId)
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.title").value(planResponse.getTitle()))
            .andExpect(jsonPath("$.description").value(planResponse.getDescription()));
    }

    @Test
    @DisplayName("존재하지 않는 플랜 ID로 정보를 조회하면 실패한다")
    void readPlanFailInvalidId() throws Exception {
        // given
        Long userId = 1L;
        Long invalidPlanId = 9999L;

        when(planService.getTotalPlanResponse(invalidPlanId)).thenThrow(new ApiException(ErrorCode.PLAN_NOT_FOUND));

        // when & then
        mockMvc.perform(get("/plans/{planId}", invalidPlanId)
                .header("X-User-Id", userId)
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("사용자를 플랜에 초대한다")
    void inviteMember() throws Exception {
        // given
        Long userId = 1L;
        Long planId = 1L;

        when(planService.inviteMember(planId, userId)).thenReturn(1L);

        // when & then
        mockMvc.perform(put("/plans/invite/{planId}", planId)
                .header("X-User-Id", userId)
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().string("1"));
    }

    @Test
    @DisplayName("존재하지 않는 플랜 ID로 사용자를 초대하면 실패한다")
    void inviteMemberFailInvalidId() throws Exception {
        // given
        Long userId = 1L;
        Long invalidPlanId = 9999L;

        when(planService.inviteMember(invalidPlanId, userId)).thenThrow(new ApiException(ErrorCode.PLAN_NOT_FOUND));

        // when & then
        mockMvc.perform(put("/invite/{planId}", invalidPlanId)
                .header("X-User-Id", userId)
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound());
    }

}
