package com.example.planservice.presentation;

import java.net.URI;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.planservice.application.TabService;
import com.example.planservice.presentation.dto.request.TabCreateRequest;
import com.example.planservice.presentation.dto.response.TabRetrieveResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/tabs")
public class TabController {
    private final TabService tabService;

    @PostMapping
    public ResponseEntity<Void> create(@Valid @RequestBody TabCreateRequest request,
                                       @RequestHeader(value = "X-User-Id", required = false) Long userId) {
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        Long createdId = tabService.create(userId, request);
        return ResponseEntity.created(URI.create("/tabs/" + createdId)).build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<TabRetrieveResponse> retrieve(@PathVariable(name = "id") Long tabId,
                                                        @RequestHeader("X-User-Id") Long userId) {
        return ResponseEntity.ok().body(tabService.retrieve(tabId, userId));
    }
}
