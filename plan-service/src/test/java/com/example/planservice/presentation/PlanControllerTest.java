package com.example.planservice.presentation;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.example.planservice.application.PlanService;
import com.example.planservice.config.JpaAuditingConfig;
import com.example.planservice.exception.ApiException;
import com.example.planservice.exception.ErrorCode;
import com.example.planservice.presentation.dto.request.PlanCreateRequest;
import com.example.planservice.presentation.dto.request.PlanUpdateRequest;
import com.example.planservice.presentation.dto.response.PlanResponse;
import com.example.planservice.presentation.dto.response.PlanTitleIdResponse;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(controllers = PlanController.class, excludeAutoConfiguration = JpaAuditingConfig.class)
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
            .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("로그인하지 않은 사용자는 플랜을 생성할 수 없다")
    void createPlanFailNotLogin() throws Exception {
        // given
        List<String> invitedEmails = new ArrayList<>();
        invitedEmails.add("A@gmail.com");
        invitedEmails.add("B@gmail.com");

        PlanCreateRequest request = PlanCreateRequest.builder()
            .title("플랜 `제목")
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

        when(planService.inviteMember("uuid", userId)).thenReturn(1L);

        // when & then
        mockMvc.perform(put("/plans/invite/{planId}", planId)
                .header("X-User-Id", userId)
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("존재하지 않는 플랜 ID로 사용자를 초대하면 실패한다")
    void inviteMemberFailInvalidId() throws Exception {
        // given
        Long userId = 1L;
        Long invalidPlanId = 9999L;

        when(planService.inviteMember("uuid", userId)).thenThrow(new ApiException(ErrorCode.PLAN_NOT_FOUND));

        // when & then
        mockMvc.perform(put("/invite/{planId}", invalidPlanId)
                .header("X-User-Id", userId)
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("플랜에서 나간다")
    void exitPlan() throws Exception {
        // given
        Long userId = 1L;
        Long planId = 1L;

        // stub
        doNothing().when(planService)
            .exit(planId, userId);

        // when & then
        mockMvc.perform(put("/plans/exit/{planId}", planId)
                .header("X-User-Id", userId)
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("존재하지 않는 플랜 ID로 플랜에서 나가면 실패한다")
    void exitPlanFailInvalidId() throws Exception {
        // given
        Long userId = 1L;
        Long invalidPlanId = 9999L;

        // stub
        Mockito.doThrow(new ApiException(ErrorCode.PLAN_NOT_FOUND))
            .when(planService)
            .exit(invalidPlanId, userId);

        // when & then
        mockMvc.perform(put("/plans/exit/{planId}", invalidPlanId)
                .header("X-User-Id", userId)
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("플랜 정보를 수정한다")
    void updatePlan() throws Exception {
        // given
        Long userId = 1L;
        Long planId = 1L;

        PlanUpdateRequest request = PlanUpdateRequest.builder()
            .title("플랜 제목")
            .intro("플랜 소개")
            .isPublic(true)
            .ownerId(userId)
            .build();

        // stub
        doNothing().when(planService)
            .update(anyLong(), any(PlanUpdateRequest.class), anyLong());

        // when & then
        mockMvc.perform(put("/plans/update/{planId}", planId)
                .header("X-User-Id", userId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("플랜을 삭제한다")
    void deletePlan() throws Exception {
        // given
        Long userId = 1L;
        Long planId = 1L;

        // stub
        doNothing().when(planService)
            .delete(planId, userId);

        // when & then
        mockMvc.perform(delete("/plans/{planId}", planId)
                .header("X-User-Id", userId)
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("존재하지 않는 플랜 ID로 플랜을 삭제하면 실패한다")
    void deletePlanFailInvalidId() throws Exception {
        // given
        Long userId = 1L;
        Long invalidPlanId = 9999L;

        // stub
        Mockito.doThrow(new ApiException(ErrorCode.PLAN_NOT_FOUND))
            .when(planService)
            .delete(invalidPlanId, userId);

        // when & then
        mockMvc.perform(delete("/plans/{planId}", invalidPlanId)
                .header("X-User-Id", userId)
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("멤버가 속한 플랜들을 조회한다")
    void readAllByMember() throws Exception {
        // given
        Long userId = 1L;
        PlanTitleIdResponse response1 = PlanTitleIdResponse.builder()
            .id(1L)
            .title("플랜 제목")
            .build();
        PlanTitleIdResponse response2 = PlanTitleIdResponse.builder()
            .id(2L)
            .title("플랜 제목")
            .build();
        // stub
        when(planService.getAllPlanTitleIdByMemberId(userId)).thenReturn(List.of(response1, response2));

        // when & then
        mockMvc.perform(get("/plans/all")
                .header("X-User-Id", userId)
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].id").value(response1.getId()))
            .andExpect(jsonPath("$[0].title").value(response1.getTitle()))
            .andExpect(jsonPath("$[1].id").value(response2.getId()))
            .andExpect(jsonPath("$[1].title").value(response2.getTitle()));

    }
}
