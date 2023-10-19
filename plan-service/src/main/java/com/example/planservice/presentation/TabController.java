package com.example.planservice.presentation;

import java.net.URI;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.planservice.application.TabService;
import com.example.planservice.application.dto.TabChangeNameResponse;
import com.example.planservice.presentation.dto.request.TabChangeNameRequest;
import com.example.planservice.presentation.dto.request.TabChangeOrderRequest;
import com.example.planservice.presentation.dto.request.TabCreateRequest;
import com.example.planservice.presentation.dto.response.ChangeOrderResponse;
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
                                       @RequestAttribute Long userId) {
        Long createdId = tabService.create(userId, request);
        return ResponseEntity.created(URI.create("/tabs/" + createdId)).build();
    }

    @PostMapping("/change-order")
    public ResponseEntity<ChangeOrderResponse> changeOrder(@Valid @RequestBody TabChangeOrderRequest request,
                                                           @RequestAttribute Long userId) {
        List<Long> sortedTabList = tabService.changeOrder(userId, request);
        return ResponseEntity.ok().body(new ChangeOrderResponse(sortedTabList));
    }

    @PatchMapping("/{tabId}/name")
    public ResponseEntity<TabChangeNameResponse> changeName(@PathVariable Long tabId,
                                                            @Valid @RequestBody TabChangeNameRequest request,
                                                            @RequestAttribute Long userId) {
        return ResponseEntity.ok().body(tabService.changeName(request.toServiceRequest(userId, tabId)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<TabRetrieveResponse> retrieve(@PathVariable(name = "id") Long tabId,
                                                        @RequestAttribute Long userId) {
        return ResponseEntity.ok().body(tabService.retrieve(tabId, userId));
    }
}
