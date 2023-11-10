package com.example.planservice.presentation;

import java.net.URI;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.planservice.application.TaskService;
import com.example.planservice.presentation.dto.request.TaskChangeOrderRequest;
import com.example.planservice.presentation.dto.request.TaskCreateRequest;
import com.example.planservice.presentation.dto.request.TaskUpdateRequest;
import com.example.planservice.presentation.dto.response.TaskFindResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/tasks")
@RequiredArgsConstructor
public class TaskController {
    private final TaskService taskService;

    @PostMapping
    public ResponseEntity<Void> create(@RequestBody @Valid TaskCreateRequest request,
                                       @RequestAttribute Long userId) {
        Long createdId = taskService.create(userId, request);
        return ResponseEntity.created(URI.create("/tasks/" + createdId)).build();
    }

    @PutMapping("/{taskId}")
    public ResponseEntity<Void> updateContents(@RequestBody @Valid TaskUpdateRequest request,
                                               @PathVariable Long taskId,
                                               @RequestAttribute Long userId) {
        taskService.updateContents(request.toServiceRequest(userId, taskId));
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/change-order")
    public ResponseEntity<Void> changeOrder(@RequestBody @Valid TaskChangeOrderRequest request,
                                            @RequestAttribute Long userId) {
        taskService.changeOrder(userId, request);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{taskId}")
    public ResponseEntity<Void> delete(@PathVariable Long taskId, @RequestAttribute Long userId) {
        taskService.delete(userId, taskId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{taskId}")
    public ResponseEntity<TaskFindResponse> find(@PathVariable Long taskId,
                                                 @RequestAttribute Long userId) {
        return ResponseEntity.ok(taskService.find(taskId, userId));
    }

}
