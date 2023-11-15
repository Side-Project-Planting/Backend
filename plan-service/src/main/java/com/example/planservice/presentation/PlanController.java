package com.example.planservice.presentation;

import java.net.URI;
import java.util.List;

import org.springframework.http.HttpStatus;
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
import com.example.planservice.presentation.dto.request.PlanKickRequest;
import com.example.planservice.presentation.dto.request.PlanUpdateRequest;
import com.example.planservice.presentation.dto.response.CreateResponse;
import com.example.planservice.presentation.dto.response.PlanResponse;
import com.example.planservice.presentation.dto.response.PlanTitleIdResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/plans")
public class PlanController {
    private final PlanService planService;

    @PostMapping
    public ResponseEntity<CreateResponse> create(@RequestBody @Valid PlanCreateRequest request,
                                                 @RequestAttribute Long userId) {
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .build();
        }
        Long createdId = planService.create(request, userId);
        return ResponseEntity.created(URI.create("/plans/"))
            .body(CreateResponse.of(createdId));
    }

    @GetMapping("/{planId}")
    public ResponseEntity<PlanResponse> read(@PathVariable Long planId, @RequestAttribute Long userId) {
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .build();
        }
        return ResponseEntity.ok(planService.getTotalPlanResponse(planId));
    }

    @PutMapping("/invite/{planId}")
    public ResponseEntity<Long> invite(@PathVariable Long planId, @RequestAttribute Long userId) {
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .build();
        }
        planService.inviteMember(planId, userId);
        return ResponseEntity.noContent()
            .build();
    }

    @PutMapping("/exit/{planId}")
    public ResponseEntity<Void> exit(@PathVariable Long planId, @RequestAttribute Long userId) {
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .build();
        }
        planService.exit(planId, userId);
        return ResponseEntity.noContent()
            .build();
    }

    @PutMapping("/kick/{planId}")
    public ResponseEntity<Void> kick(@PathVariable Long planId, @RequestBody @Valid PlanKickRequest request,
                                     @RequestAttribute Long userId) {
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .build();
        }
        planService.kick(planId, request, userId);
        return ResponseEntity.noContent()
            .build();
    }

    @PutMapping("/update/{planId}")
    public ResponseEntity<Long> update(@PathVariable Long planId, @RequestBody @Valid PlanUpdateRequest request,
                                       @RequestAttribute Long userId) {
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .build();
        }
        planService.update(planId, request, userId);
        return ResponseEntity.noContent()
            .build();
    }

    @DeleteMapping("/{planId}")
    public ResponseEntity<Void> delete(@PathVariable Long planId, @RequestAttribute Long userId) {
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .build();
        }
        planService.delete(planId, userId);
        return ResponseEntity.noContent()
            .build();
    }

    @GetMapping("/all")
    public ResponseEntity<List<PlanTitleIdResponse>> readAllByMember(@RequestAttribute Long userId) {
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .build();
        }
        return ResponseEntity.ok(planService.getAllPlanTitleIdByMemberId(userId));
    }

}
