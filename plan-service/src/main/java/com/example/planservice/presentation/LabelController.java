package com.example.planservice.presentation;

import java.net.URI;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.planservice.application.LabelService;
import com.example.planservice.presentation.dto.request.LabelCreateRequest;
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
        Long createdId = labelService.create(labelCreateRequest.getName(), labelCreateRequest.getPlanId(), userId);
        return ResponseEntity.created(URI.create("/labels/" + createdId)).build();
    }
}
