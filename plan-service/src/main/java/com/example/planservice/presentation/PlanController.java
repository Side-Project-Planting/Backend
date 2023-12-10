package com.example.planservice.presentation;

import java.net.URI;
import java.util.List;

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

import com.example.planservice.application.PlanService;
import com.example.planservice.presentation.dto.request.PlanCreateRequest;
import com.example.planservice.presentation.dto.request.PlanUpdateRequest;
import com.example.planservice.presentation.dto.response.CreateResponse;
import com.example.planservice.presentation.dto.response.PlanMainResponse;
import com.example.planservice.presentation.dto.response.PlanResponse;
import com.example.planservice.presentation.dto.response.PlanTitleIdResponse;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Tag(name = "플랜")
@RestController
@RequiredArgsConstructor
@RequestMapping("/plans")
public class PlanController {
    private final PlanService planService;

    @PostMapping
    @ApiResponse(responseCode = "201", description = "플랜 생성 성공",
        content = @Content(mediaType = "application/json",
            schema = @Schema(implementation = CreateResponse.class)))
    public ResponseEntity<CreateResponse> create(@RequestBody @Valid PlanCreateRequest request,
                                                 @RequestAttribute Long userId) {
        Long createdId = planService.create(request, userId);
        return ResponseEntity.created(URI.create("/plans/"))
            .body(CreateResponse.of(createdId));
    }

    @GetMapping("/{planId}")
    @ApiResponse(responseCode = "200", description = "플랜 조회 성공",
        content = @Content(mediaType = "application/json",
            schema = @Schema(implementation = PlanResponse.class)))
    public ResponseEntity<PlanResponse> read(@PathVariable Long planId, @RequestAttribute Long userId) {
        return ResponseEntity.ok(planService.getTotalPlanResponse(planId));
    }

    // TODO 해당 API Swagger 작업하기
    @GetMapping("/main")
    public ResponseEntity<List<PlanMainResponse>> readAll(@RequestAttribute Long userId) {
        return ResponseEntity.ok(planService.getMainResponse(userId));
    }

    @PutMapping("/invite/{uuid}")
    @ApiResponse(responseCode = "204", description = "플랜 수정 성공")
    public ResponseEntity<Long> invite(@PathVariable String uuid, @RequestAttribute Long userId) {
        planService.inviteMember(uuid, userId);
        return ResponseEntity.noContent()
            .build();
    }

    @PutMapping("/exit/{planId}")
    @ApiResponse(responseCode = "204", description = "플랜 나가기 성공")
    public ResponseEntity<Void> exit(@PathVariable Long planId, @RequestAttribute Long userId) {
        planService.exit(planId, userId);
        return ResponseEntity.noContent()
            .build();
    }

    @PutMapping("/update/{planId}")
    @ApiResponse(responseCode = "204", description = "플랜 정보 수정 성공")
    public ResponseEntity<Void> update(@PathVariable Long planId, @RequestBody @Valid PlanUpdateRequest request,
                                       @RequestAttribute Long userId) {
        planService.update(planId, request, userId);
        return ResponseEntity.noContent()
            .build();
    }

    @DeleteMapping("/{planId}")
    @ApiResponse(responseCode = "204", description = "플랜 삭제 성공")
    public ResponseEntity<Void> delete(@PathVariable Long planId, @RequestAttribute Long userId) {
        planService.delete(planId, userId);
        return ResponseEntity.noContent()
            .build();
    }

    @GetMapping("/all")
    @ApiResponse(responseCode = "200", description = "플랜에 속한 전체 사용자 조회 성공",
        content = @Content(mediaType = "application/json",
            schema = @Schema(implementation = PlanTitleIdResponse.class)))
    public ResponseEntity<List<PlanTitleIdResponse>> readAllByMember(@RequestAttribute Long userId) {
        return ResponseEntity.ok(planService.getAllPlanTitleIdByMemberId(userId));
    }

}
