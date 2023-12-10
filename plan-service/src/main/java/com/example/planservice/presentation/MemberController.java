package com.example.planservice.presentation;

import java.net.URI;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.planservice.application.MemberService;
import com.example.planservice.application.dto.MemberRegisterResponse;
import com.example.planservice.presentation.dto.request.MemberRegisterRequest;
import com.example.planservice.presentation.dto.response.MemberFindResponse;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Tag(name = "회원")
@RestController
@RequestMapping("/members")
@RequiredArgsConstructor
public class MemberController {
    private final MemberService memberService;

    @PostMapping
    @ApiResponse(responseCode = "201", description = "회원 생성 성공")
    ResponseEntity<MemberRegisterResponse> register(@RequestBody @Valid MemberRegisterRequest request) {
        MemberRegisterResponse response = memberService.register(request);
        return ResponseEntity.created(URI.create("/members/" + response.getId())).body(response);
    }

    @GetMapping("/{id}")
    @ApiResponse(responseCode = "200", description = "회원 조회 성공",
        content = @Content(mediaType = "application/json",
            schema = @Schema(implementation = MemberFindResponse.class)))
    ResponseEntity<MemberFindResponse> find(@PathVariable Long id) {
        return ResponseEntity.ok(memberService.find(id));
    }
}
