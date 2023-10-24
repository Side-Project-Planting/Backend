package com.example.planservice.presentation;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrlPattern;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
import com.example.planservice.presentation.dto.request.PlanCreateRequest;
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
        final Long userId = 1L;
        final List<String> invitedEmails = List.of("test@example.com");
        final PlanCreateRequest request = PlanCreateRequest.builder()
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
        final List<String> invitedEmails = new ArrayList<>();
        invitedEmails.add("A@gmail.com");
        invitedEmails.add("B@gmail.com");

        final PlanCreateRequest request = PlanCreateRequest.builder()
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
        final Long userId = 1L;
        final List<String> invalidEmails = List.of("invalid-email");

        final PlanCreateRequest request = PlanCreateRequest.builder()
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
}
