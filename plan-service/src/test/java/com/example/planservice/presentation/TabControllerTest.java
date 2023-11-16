package com.example.planservice.presentation;


import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;

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
import com.example.planservice.application.dto.TabChangeNameResponse;
import com.example.planservice.application.dto.TabChangeNameServiceRequest;
import com.example.planservice.application.dto.TabDeleteServiceRequest;
import com.example.planservice.presentation.dto.request.TabChangeTitleRequest;
import com.example.planservice.presentation.dto.request.TabChangeOrderRequest;
import com.example.planservice.presentation.dto.request.TabCreateRequest;
import com.example.planservice.presentation.dto.response.TabFindResponse;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(controllers = {TabController.class})
class TabControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    TabService tabService;

    @Test
    @DisplayName("Tab을 생성한다")
    void testCreateTab() throws Exception {
        // given
        Long userId = 1L;
        Long planId = 10L;
        TabCreateRequest request = TabCreateRequest.builder()
            .name("탭이름")
            .planId(planId)
            .build();
        Long createdTabId = 2L;

        // stub
        when(tabService.create(anyLong(), any(TabCreateRequest.class)))
            .thenReturn(createdTabId);

        // when & then
        mockMvc.perform(post("/tabs")
                .header("X-User-Id", userId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated())
            .andExpect(header().string("Location", "/tabs/" + createdTabId));
    }

    @Test
    @DisplayName("로그인하지 않은 사용자는 Tab을 생성할 수 없다")
    void testCreateTabFailNotLogin() throws Exception {
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
    void testFindTab() throws Exception {
        // given
        Long targetTabId = 1L;
        Long userId = 2L;
        TabFindResponse response = TabFindResponse.builder()
            .id(targetTabId)
            .name("탭명")
            .nextId(null)
            .build();

        // stub
        when(tabService.find(targetTabId, userId))
            .thenReturn(response);

        // when & then
        mockMvc.perform(get("/tabs/" + targetTabId)
                .header("X-User-Id", userId))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(response.getId()))
            .andExpect(jsonPath("$.name").value(response.getName()))
            .andExpect(jsonPath("$.nextId").value(response.getNextId()));
    }

    @Test
    @DisplayName("로그인한 사용자만 탭을 조회할 수 있다")
    void testFindTabFailNotLogin() throws Exception {
        // given
        Long targetTabId = 1L;
        Long userId = 2L;

        // when & then
        mockMvc.perform(get("/tabs/" + targetTabId))
            .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Tab의 순서를 변경한다")
    void changeTabOrder() throws Exception {
        // given
        Long userId = 1L;
        TabChangeOrderRequest request = TabChangeOrderRequest.builder().build();

        // stub
        when(tabService.changeOrder(anyLong(), any(TabChangeOrderRequest.class)))
            .thenReturn(List.of(2L, 1L));

        // when & then
        mockMvc.perform(post("/tabs/change-order")
                .header("X-User-Id", userId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.sortedTabs").isArray())
            .andExpect(jsonPath("$.sortedTabs[0]").value(2L))
            .andExpect(jsonPath("$.sortedTabs[1]").value(1L));
    }

    @Test
    @DisplayName("인증되지 않은 사용자는 Tab의 순서를 변경할 수 없다")
    void changeTabOrderFailNotAuthenticated() throws Exception {
        // given
        TabChangeOrderRequest request = TabChangeOrderRequest.builder().build();

        // stub
        when(tabService.changeOrder(anyLong(), any(TabChangeOrderRequest.class)))
            .thenReturn(List.of(2L, 1L));

        // when & then
        mockMvc.perform(post("/tabs/change-order")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Tab의 이름을 변경한다")
    void changeTabName() throws Exception {
        // given
        Long userId = 1L;
        TabChangeTitleRequest request = TabChangeTitleRequest.builder()
            .build();
        TabChangeNameResponse response = TabChangeNameResponse.builder()
            .name("변경된이름")
            .id(3L)
            .build();

        // stub
        when(tabService.changeName(any(TabChangeNameServiceRequest.class)))
            .thenReturn(response);

        // when & then
        mockMvc.perform(patch("/tabs/1/title")
                .header("X-User-Id", userId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(response.getId()))
            .andExpect(jsonPath("$.name").value(response.getName()));
    }

    @Test
    @DisplayName("인증되지 않은 사용자는 Tab의 이름을 변경할 수 없다")
    void changeTabNameFailNotAuthenticated() throws Exception {
        // given
        TabChangeTitleRequest request = TabChangeTitleRequest.builder().build();
        TabChangeNameResponse response = TabChangeNameResponse.builder().build();

        // stub
        when(tabService.changeName(any(TabChangeNameServiceRequest.class)))
            .thenReturn(response);

        // when & then
        mockMvc.perform(patch("/tabs/1/name")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("탭 삭제에 성공하면 204 상태를 반환한다")
    void deleteTab() throws Exception {
        // given
        Long userId = 1L;
        Long tabId = 2L;
        Long planId = 3L;

        // stub
        when(tabService.delete(any(TabDeleteServiceRequest.class)))
            .thenReturn(tabId);

        // when & then
        mockMvc.perform(delete("/tabs/" + tabId + "?planId=" + planId)
                .header("X-User-Id", userId))
            .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("탭 삭제는 로그인한 사용자만이 가능하다")
    void deleteTabFailNotAuthenticated() throws Exception {
        // given
        Long tabId = 2L;
        Long planId = 3L;

        // when & then
        mockMvc.perform(delete("/tabs/" + tabId + "?planId=" + planId))
            .andExpect(status().isUnauthorized());
    }
}
