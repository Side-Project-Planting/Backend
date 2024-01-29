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
import org.springframework.test.web.servlet.MockMvc;

import com.example.planservice.application.TaskService;
import com.example.planservice.config.JpaAuditingConfig;
import com.example.planservice.presentation.dto.request.TaskCreateRequest;
import com.example.planservice.presentation.dto.response.TaskFindResponse;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(controllers = TaskController.class, excludeAutoConfiguration = JpaAuditingConfig.class)
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
            .title("이름")
            .build();
        Long createdId = 1L;
        Long userId = 2L;

        // stub
        when(taskService.create(anyLong(), any(TaskCreateRequest.class)))
            .thenReturn(createdId);

        // when & then
        mockMvc.perform(post("/tasks")
                .header("X-User-Id", userId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated())
            .andExpect(header().string("Location", "/tasks/" + createdId))
            .andExpect(redirectedUrlPattern("/tasks/*"))
            .andExpect(jsonPath("$.id").value(createdId));;
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
            .title("이름")
            .build();
        Long createdId = 1L;
        Long userId = 2L;

        // stub
        when(taskService.create(anyLong(), any(TaskCreateRequest.class)))
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
            .title("이름")
            .build();
        Long createdId = 1L;
        Long userId = 2L;

        // stub
        when(taskService.create(anyLong(), any(TaskCreateRequest.class)))
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
            .title("")
            .build();
        Long createdId = 1L;
        Long userId = 2L;

        // stub
        when(taskService.create(anyLong(), any(TaskCreateRequest.class)))
            .thenReturn(createdId);

        // when & then
        mockMvc.perform(post("/tasks")
                .header("X-User-Id", userId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("태스크를 삭제한다")
    void testDeleteTask() throws Exception {
        // given
        Long userId = 2L;
        Long deletedTaskId = 1L;

        // when & then
        mockMvc.perform(delete("/tasks/" + deletedTaskId)
                .header("X-User-Id", userId))
            .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("로그인하지 않은 사용자는 태스크를 삭제할 수 없다")
    void testCreateTaskFailNotLogin() throws Exception {
        Long deletedTaskId = 1L;

        // when & then
        mockMvc.perform(delete("/tasks/" + deletedTaskId))
            .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("태스크를 조회한다")
    void testFindTask() throws Exception {
        // given
        Long userId = 2L;
        Long taskId = 1L;

        TaskFindResponse response = TaskFindResponse.builder()
            .id(taskId)
            .title("태스크제목")
            .build();

        // stub
        when(taskService.find(anyLong(), anyLong()))
            .thenReturn(response);

        // when & then
        mockMvc.perform(get("/tasks/" + taskId)
                .header("X-User-Id", userId))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(response.getId()))
            .andExpect(jsonPath("$.title").value(response.getTitle()));
    }

    @Test
    @DisplayName("로그인하지 않은 사용자는 태스크를 조회할 수 없다")
    void testFindTaskFailNotLogin() throws Exception {
        Long taskId = 1L;

        // when & then
        mockMvc.perform(get("/tasks/" + taskId))
            .andExpect(status().isUnauthorized());
    }
}
