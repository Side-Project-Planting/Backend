package com.example.planservice.presentation;

import java.net.URI;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
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
import com.example.planservice.presentation.dto.response.CreateResponse;
import com.example.planservice.presentation.dto.response.TaskFindResponse;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;


@Tag(name = "태스크")
@RestController
@RequestMapping("/tasks")
@RequiredArgsConstructor
public class TaskController {
    private final TaskService taskService;

    @PostMapping
    @ApiResponse(responseCode = "201", description = "태스크 생성 성공")
    public ResponseEntity<CreateResponse> create(@RequestBody @Valid TaskCreateRequest request,
                                                 @RequestAttribute Long userId) {
        Long createdId = taskService.create(userId, request);
        return ResponseEntity.created(URI.create("/tasks/" + createdId))
            .body(CreateResponse.of(createdId));
    }

    @PutMapping("/{taskId}")
    @ApiResponse(responseCode = "204", description = "태스크 수정 성공")
    public ResponseEntity<Void> updateContents(@RequestBody @Valid TaskUpdateRequest request,
                                               @PathVariable Long taskId,
                                               @RequestAttribute Long userId) {
        taskService.updateContents(request.toServiceRequest(userId, taskId));
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/change-order")
    @ApiResponse(responseCode = "204", description = "태스크 순서 변경 성공")
    public ResponseEntity<Void> changeOrder(@RequestBody @Valid TaskChangeOrderRequest request,
                                            @RequestAttribute Long userId) {
        taskService.changeOrder(userId, request);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{taskId}")
    @ApiResponse(responseCode = "204", description = "태스크 삭제 성공")
    public ResponseEntity<Void> delete(@PathVariable Long taskId, @RequestAttribute Long userId) {
        taskService.delete(userId, taskId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{taskId}")
    @PatchMapping("/{tabId}/title")
    @ApiResponse(responseCode = "200", description = "태스크 조회 성공",
        content = @Content(mediaType = "application/json",
            schema = @Schema(implementation = TaskFindResponse.class)))
    public ResponseEntity<TaskFindResponse> find(@PathVariable Long taskId,
                                                 @RequestAttribute Long userId) {
        return ResponseEntity.ok(taskService.find(taskId, userId));
    }
}
