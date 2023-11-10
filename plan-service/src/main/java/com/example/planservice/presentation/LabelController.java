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
import com.example.planservice.presentation.dto.response.LabelFindResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/labels")
@RequiredArgsConstructor
public class LabelController {
    private final LabelService labelService;

    @PostMapping
    ResponseEntity<Void> create(@RequestBody @Valid LabelCreateRequest labelCreateRequest,
                                @RequestAttribute Long userId) {
        Long createdId = labelService.create(userId, labelCreateRequest);
        return ResponseEntity.created(URI.create("/labels/" + createdId)).build();
    }

    @DeleteMapping("/{labelId}")
    ResponseEntity<Void> delete(@PathVariable Long labelId,
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
    public ResponseEntity<LabelFindResponse> find(@PathVariable(name = "id") Long labelId,
                                                  @RequestAttribute Long userId) {
        return ResponseEntity.ok().body(labelService.find(labelId, userId));
    }

}
