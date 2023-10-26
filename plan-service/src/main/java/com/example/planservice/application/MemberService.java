package com.example.planservice.application;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.planservice.application.dto.MemberRegisterResponse;
import com.example.planservice.domain.member.Member;
import com.example.planservice.domain.member.repository.MemberRepository;
import com.example.planservice.presentation.dto.request.MemberRegisterRequest;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService {
    private final MemberRepository memberRepository;

    @Transactional
    public MemberRegisterResponse register(MemberRegisterRequest request) {
        Member member = request.toEntity();
        memberRepository.save(member);
        return MemberRegisterResponse.of(member);
    }
}
