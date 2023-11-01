package com.example.planservice.domain.member.repository;

import com.example.planservice.domain.member.Member;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class MemberRepositoryTest {
    @Autowired
    MemberRepository memberRepository;

    @Test
    @DisplayName("이메일로 멤버를 찾는다")
    void testFindByEmailSuccess() {
        // given
        Member member = Member.builder().email("a@naver.com").build();
        memberRepository.save(member);

        // when
        Optional<Member> resultOpt = memberRepository.findByEmail("a@naver.com");

        // then
        assertThat(resultOpt.get().getId()).isEqualTo(member.getId());
    }

    @Test
    @DisplayName("이메일로 조회 시 멤버가 존재하지 않으면 Optional.empty()를 반환한다")
    void testFindByEmailFailNotFound() {
        // when
        Optional<Member> resultOpt = memberRepository.findByEmail("a@naver.com");

        // then
        assertThat(resultOpt).isEmpty();
    }

}