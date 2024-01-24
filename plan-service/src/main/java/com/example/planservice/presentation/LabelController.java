package com.example.planservice.presentation;

import java.net.URI;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.planservice.application.LabelService;
import com.example.planservice.application.dto.LabelDeleteServiceRequest;
import com.example.planservice.presentation.dto.request.LabelCreateRequest;
import com.example.planservice.presentation.dto.response.CreateResponse;
import com.example.planservice.presentation.dto.response.LabelFindResponse;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Tag(name = "라벨")
@RestController
@RequestMapping("/labels")
@RequiredArgsConstructor
public class LabelController {
    private final LabelService labelService;

    @PostMapping
    @ApiResponse(responseCode = "201", description = "라벨 생성 성공")
    ResponseEntity<CreateResponse> create(@RequestBody @Valid LabelCreateRequest labelCreateRequest,
                                          @RequestAttribute Long userId) {
        Long createdId = labelService.create(userId, labelCreateRequest);
        return ResponseEntity.created(URI.create("/labels/" + createdId))
            .body(CreateResponse.of(createdId));
    }

    @DeleteMapping("/{id}")
    @ApiResponse(responseCode = "204", description = "라벨 삭제 성공")
    ResponseEntity<Void> delete(@PathVariable(name = "id") Long labelId,
                                @RequestParam Long planId,
                                @RequestAttribute Long userId) {
        LabelDeleteServiceRequest request = LabelDeleteServiceRequest.builder()
            .labelId(labelId)
            .planId(planId)
            .memberId(userId)
            .build();

        labelService.delete(request);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    @PostMapping
    @ApiResponse(responseCode = "200", description = "라벨 조회 성공",
        content = @Content(mediaType = "application/json",
            schema = @Schema(implementation = LabelFindResponse.class)))
    public ResponseEntity<LabelFindResponse> find(@PathVariable(name = "id") Long labelId,
                                                  @RequestAttribute Long userId) {
        return ResponseEntity.ok().body(labelService.find(labelId, userId));
    }

}
