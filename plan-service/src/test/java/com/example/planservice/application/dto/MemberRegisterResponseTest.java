package com.example.planservice.application.dto;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import com.example.planservice.domain.member.Member;
import jakarta.persistence.EntityManager;

@SpringBootTest
@Transactional
class MemberRegisterResponseTest {
    @Autowired
    EntityManager em;

    @Test
    @DisplayName("Member 엔티티로 MemberRegisterResponse를 생성한다")
    void createMemberRegisterResponse() {
        // given
        Member member = Member.builder()
            .name("ds")
            .email("a@naver.com")
            .profileUri("https://2adsad@dsa.com")
            .receiveEmails(false)
            .build();
        em.persist(member);

        // when
        MemberRegisterResponse result = MemberRegisterResponse.of(member);

        assertThat(result.getId()).isEqualTo(member.getId());
        assertThat(result.getName()).isEqualTo(member.getName());
        assertThat(result.getEmail()).isEqualTo(member.getEmail());
        assertThat(result.getProfileUri()).isEqualTo(member.getProfileUri());
        assertThat(result.isReceiveEmails()).isEqualTo(member.isReceiveEmails());
    }

}
