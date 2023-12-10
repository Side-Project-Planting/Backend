package com.example.planservice.presentation;

import java.net.URI;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.planservice.application.TabService;
import com.example.planservice.application.dto.TabChangeTitleResponse;
import com.example.planservice.application.dto.TabDeleteServiceRequest;
import com.example.planservice.presentation.dto.request.TabChangeOrderRequest;
import com.example.planservice.presentation.dto.request.TabChangeTitleRequest;
import com.example.planservice.presentation.dto.request.TabCreateRequest;
import com.example.planservice.presentation.dto.response.TabChangeOrderResponse;
import com.example.planservice.presentation.dto.response.TabFindResponse;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Tag(name = "탭")
@RestController
@RequiredArgsConstructor
@RequestMapping("/tabs")
public class TabController {
    private final TabService tabService;

    @PostMapping
    @ApiResponse(responseCode = "201", description = "탭 생성 성공")
    public ResponseEntity<Void> create(@Valid @RequestBody TabCreateRequest request,
                                       @RequestAttribute Long userId) {
        Long createdId = tabService.create(userId, request);
        return ResponseEntity.created(URI.create("/tabs/" + createdId)).build();
    }

    @PostMapping("/change-order")
    @ApiResponse(responseCode = "200", description = "탭 순서 변경 성공",
        content = @Content(mediaType = "application/json",
            schema = @Schema(implementation = TabChangeOrderResponse.class)))
    public ResponseEntity<TabChangeOrderResponse> changeOrder(@Valid @RequestBody TabChangeOrderRequest request,
                                                              @RequestAttribute Long userId) {
        List<Long> sortedTabList = tabService.changeOrder(userId, request);
        return ResponseEntity.ok().body(new TabChangeOrderResponse(sortedTabList));
    }

    @PatchMapping("/{tabId}/title")
    @ApiResponse(responseCode = "200", description = "탭 제목 변경 성공",
        content = @Content(mediaType = "application/json",
            schema = @Schema(implementation = TabChangeTitleResponse.class)))
    public ResponseEntity<TabChangeTitleResponse> changeTitle(@PathVariable Long tabId,
                                                              @Valid @RequestBody TabChangeTitleRequest request,
                                                              @RequestAttribute Long userId) {
        return ResponseEntity.ok().body(tabService.changeName(request.toServiceRequest(userId, tabId)));
    }

    @GetMapping("/{id}")
    @ApiResponse(responseCode = "200", description = "탭 조회 성공")
    @PatchMapping("/{tabId}/title")
    @ApiResponse(responseCode = "200", description = "탭 조회 성공",
        content = @Content(mediaType = "application/json",
            schema = @Schema(implementation = TabFindResponse.class)))
    public ResponseEntity<TabFindResponse> find(@PathVariable(name = "id") Long tabId,
                                                @RequestAttribute Long userId) {
        return ResponseEntity.ok().body(tabService.find(tabId, userId));
    }

    @DeleteMapping("/{id}")
    @ApiResponse(responseCode = "204", description = "탭 삭제 성공")
    public ResponseEntity<Void> delete(@PathVariable(name = "id") Long tabId,
                                       @RequestAttribute Long userId,
                                       @RequestParam Long planId) {
        TabDeleteServiceRequest request = TabDeleteServiceRequest.builder()
            .tabId(tabId)
            .memberId(userId)
            .planId(planId)
            .build();
        tabService.delete(request);
        return ResponseEntity.noContent().build();
    }
}
