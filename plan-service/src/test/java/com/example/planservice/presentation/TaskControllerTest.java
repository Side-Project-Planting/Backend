package com.example.planservice.presentation;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.example.planservice.application.TaskService;
import com.example.planservice.presentation.dto.request.TaskCreateRequest;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(controllers = {TaskController.class})
class TaskControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    TaskService taskService;

    @Test
    @DisplayName("태스크를 생성한다")
    void createTask() throws Exception {
        // given
        TaskCreateRequest request = TaskCreateRequest.builder()
            .planId(1L)
            .tabId(1L)
            .name("이름")
            .build();
        Long createdId = 1L;
        Long userId = 2L;

        // stub
        Mockito.when(taskService.create(anyLong(), any(TaskCreateRequest.class)))
            .thenReturn(createdId);

        // when & then
        mockMvc.perform(post("/tasks")
                .header("X-User-Id", userId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated())
            .andExpect(header().string("Location", "/tasks/" + createdId))
            .andExpect(redirectedUrlPattern("/tasks/*"));
    }

    @Test
    @DisplayName("로그인하지 않은 사용자는 태스크를 생성할 수 없다")
    void createTaskFailNotLogin() throws Exception {
        // given
        TaskCreateRequest request = TaskCreateRequest.builder().build();

        // when & then
        mockMvc.perform(post("/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("태스크 생성 시 planId는 필수다")
    void testCreateTaskPlanIdParameterIsNecessary() throws Exception {
        // given
        TaskCreateRequest request = TaskCreateRequest.builder()
            .tabId(1L)
            .name("이름")
            .build();
        Long createdId = 1L;
        Long userId = 2L;

        // stub
        Mockito.when(taskService.create(anyLong(), any(TaskCreateRequest.class)))
            .thenReturn(createdId);

        // when & then
        mockMvc.perform(post("/tasks")
                .header("X-User-Id", userId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("태스크 생성 시 tabId는 필수다")
    void testCreateTaskTabIdParameterIsNecessary() throws Exception {
        // given
        TaskCreateRequest request = TaskCreateRequest.builder()
            .planId(1L)
            .name("이름")
            .build();
        Long createdId = 1L;
        Long userId = 2L;

        // stub
        Mockito.when(taskService.create(anyLong(), any(TaskCreateRequest.class)))
            .thenReturn(createdId);

        // when & then
        mockMvc.perform(post("/tasks")
                .header("X-User-Id", userId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("태스크 생성 시 name은 필수다")
    void testCreateTaskNameParameterIsNecessary() throws Exception {
        // given
        TaskCreateRequest request = TaskCreateRequest.builder()
            .planId(1L)
            .tabId(1L)
            .name("")
            .build();
        Long createdId = 1L;
        Long userId = 2L;

        // stub
        Mockito.when(taskService.create(anyLong(), any(TaskCreateRequest.class)))
            .thenReturn(createdId);

        // when & then
        mockMvc.perform(post("/tasks")
                .header("X-User-Id", userId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest());
    }

}
