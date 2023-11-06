package com.example.planservice.application;

import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.planservice.application.dto.MemberRegisterResponse;
import com.example.planservice.domain.member.Member;
import com.example.planservice.domain.member.repository.MemberRepository;
import com.example.planservice.exception.ApiException;
import com.example.planservice.exception.ErrorCode;
import com.example.planservice.presentation.dto.request.MemberRegisterRequest;
import com.example.planservice.presentation.dto.response.MemberFindResponse;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService {
    private final MemberRepository memberRepository;

    @Transactional
    public MemberRegisterResponse register(MemberRegisterRequest request) {
        Optional<Member> memberOpt = memberRepository.findByEmail(request.getEmail());
        if (memberOpt.isPresent()) {
            throw new ApiException(ErrorCode.ALREADY_REGISTERED);
        }

        Member member = request.toEntity();
        memberRepository.save(member);
        return MemberRegisterResponse.of(member);
    }

    public MemberFindResponse find(Long id) {
        Member member = memberRepository.findById(id)
            .orElseThrow(() -> new ApiException(ErrorCode.MEMBER_NOT_FOUND));
        if (!member.isNormalUser()) {
            throw new ApiException(ErrorCode.MEMBER_NOT_FOUND);
        }
        return MemberFindResponse.from(member);
    }
}
