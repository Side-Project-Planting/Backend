package com.example.planservice.presentation;


import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.example.planservice.application.TabService;
import com.example.planservice.exception.ApiException;
import com.example.planservice.exception.ErrorCode;
import com.example.planservice.presentation.dto.request.TabChangeOrderRequest;
import com.example.planservice.presentation.dto.request.TabCreateRequest;
import com.example.planservice.presentation.dto.response.TabRetrieveResponse;
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
        Long planId = 10L;
        TabCreateRequest request = TabCreateRequest.builder()
            .name("탭이름")
            .planId(planId)
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

    @Test
    @DisplayName("로그인하지 않은 사용자는 Tab을 생성할 수 없다")
    void createtabFailNotLogin() throws Exception {
        // given
        Long planId = 10L;
        TabCreateRequest request = TabCreateRequest.builder()
            .name("탭이름")
            .planId(planId)
            .build();

        // when & then
        mockMvc.perform(post("/tabs")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isUnauthorized());
    }

    // TODO 해당 테스트가 필요할까요? TAB_SIZE_LIMIT Api Exception이 터지면 400번 응답이 나오는지 테스트하는 로직인데
    //  ErrorCode와 관련해서 테스트가 들어가는게 맞다는 생각이 듭니다.
    @Test
    @DisplayName("하나의 플랜에 Tab은 최대 5개까지만 생성이 가능하다")
    void createTabFailSizeOver() throws Exception {
        // given
        Long userId = 1L;
        Long planId = 10L;
        TabCreateRequest request = TabCreateRequest.builder()
            .name("탭이름")
            .planId(planId)
            .build();

        when(tabService.create(anyLong(), any(TabCreateRequest.class)))
            .thenThrow(new ApiException(ErrorCode.TAB_SIZE_LIMIT));

        // when & then
        mockMvc.perform(post("/tabs")
                .header("X-User-Id", userId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest());
    }

    @ParameterizedTest
    @DisplayName("탭의 이름은 공백이 될 수 없다")
    @ValueSource(strings = {"", " ", "  "})
    void createTabFailTabNameBlank(String name) throws Exception {
        // given
        Long userId = 1L;
        Long planId = 10L;
        TabCreateRequest request = TabCreateRequest.builder()
            .name(name)
            .planId(planId)
            .build();

        // when & then
        mockMvc.perform(post("/tabs")
                .header("X-User-Id", userId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("탭의 이름은 null이 될 수 없다")
    void createTabFailTabNameNull() throws Exception {
        // given
        Long userId = 1L;
        Long planId = 10L;
        TabCreateRequest request = TabCreateRequest.builder()
            .planId(planId)
            .build();

        // when & then
        mockMvc.perform(post("/tabs")
                .header("X-User-Id", userId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("탭을 등록할 때 Project Id는 필수다")
    void createTabFailEmptyProjectId() throws Exception {
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
            .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("탭을 조회한다")
    void retrieveTab() throws Exception {
        // given
        Long userId = 1L;
        Long tabId = 10L;
        TabRetrieveResponse response = TabRetrieveResponse.builder()
            .tabId(tabId)
            .build();

        when(tabService.retrieve(tabId, userId))
            .thenReturn(response);

        mockMvc.perform(get("/tabs/" + tabId)
                .header("X-User-Id", userId))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.tabId").value(tabId));
    }

    @Test
    @DisplayName("로그인하지 않은 사용자는 Tab을 가져올 수 없다")
    void retrieveTabFailNotLogin() throws Exception {
        // given
        Long tabId = 10L;

        // when & then
        mockMvc.perform(get("/tabs/" + tabId)
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Tab의 순서를 변경한다")
    void changeTabOrder() throws Exception {
        // given
        Long userId = 1L;
        Long planId = 10L;
        TabChangeOrderRequest request = TabChangeOrderRequest.builder().build();

        // when & then
        mockMvc.perform(post("/tabs/change-order")
                .header("X-User-Id", userId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk());
    }
}