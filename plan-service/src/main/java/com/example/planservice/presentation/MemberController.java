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
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/members")
@RequiredArgsConstructor
public class MemberController {
    private final MemberService memberService;

    @PostMapping
    ResponseEntity<MemberRegisterResponse> register(@RequestBody @Valid MemberRegisterRequest request) {
        MemberRegisterResponse response = memberService.register(request);
        return ResponseEntity.created(URI.create("/members/" + response.getId())).body(response);
    }

    @GetMapping("/{id}")
    ResponseEntity<MemberFindResponse> find(@PathVariable Long id) {
        return ResponseEntity.ok(memberService.find(id));
    }
}
