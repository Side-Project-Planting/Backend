package com.example.planservice.presentation;

import java.net.URI;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.planservice.application.TaskService;
import com.example.planservice.presentation.dto.request.TaskCreateRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/tasks")
@RequiredArgsConstructor
public class TaskController {
    private final TaskService taskService;

    @PostMapping
    public ResponseEntity<Void> create(@RequestBody @Valid TaskCreateRequest request,
                                       @RequestAttribute Long userId) {
        Long createdId = taskService.createTask(userId, request);
        return ResponseEntity.created(URI.create("/tasks/" + createdId)).build();
    }

}
