package com.example.planservice.presentation;

import java.net.URI;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.planservice.application.TabService;
import com.example.planservice.presentation.dto.request.TabCreateRequest;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/tabs")
public class TabController {
    private final TabService tabService;

    @PostMapping
    public ResponseEntity<Void> create(@RequestBody TabCreateRequest request,
                                       @RequestHeader("X-User-Id") Long userId) {
        Long createdId = tabService.create(userId, request);
        return ResponseEntity.created(URI.create("/tabs/" + createdId)).build();
    }

}
