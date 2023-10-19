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
        TaskCreateRequest request = TaskCreateRequest.builder().build();
        Long createdId = 1L;
        Long userId = 2L;

        // stub
        Mockito.when(taskService.createTask(anyLong(), any(TaskCreateRequest.class)))
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
}