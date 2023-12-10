package com.example.planservice.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import com.example.planservice.application.dto.MemberRegisterResponse;
import com.example.planservice.domain.member.Member;
import com.example.planservice.domain.member.Role;
import com.example.planservice.exception.ApiException;
import com.example.planservice.exception.ErrorCode;
import com.example.planservice.presentation.dto.request.MemberRegisterRequest;
import com.example.planservice.presentation.dto.response.MemberFindResponse;
import jakarta.persistence.EntityManager;

@SpringBootTest
@Transactional
class MemberServiceTest {
    @Autowired
    MemberService memberService;

    @Autowired
    EntityManager em;

    @Test
    @DisplayName("멤버를 등록한다")
    void testRegister() throws Exception {
        // given
        String email = "a@naver.ocm";
        String profileUri = "https:/sd2sw2.com";
        String name = "김태훈";
        boolean receiveEmails = false;

        MemberRegisterRequest request = MemberRegisterRequest.builder()
            .email(email)
            .profileUri(profileUri)
            .name(name)
            .receiveEmails(receiveEmails)
            .build();

        // when
        MemberRegisterResponse response = memberService.register(request);

        // then
        assertThat(response.getId()).isNotNull();
        assertThat(response.getEmail()).isEqualTo(email);
        assertThat(response.getProfileUri()).isEqualTo(profileUri);
        assertThat(response.getName()).isEqualTo(name);
        assertThat(response.isReceiveEmails()).isEqualTo(receiveEmails);

        Member result = em.find(Member.class, response.getId());
        assertThat(result.getEmail()).isEqualTo(email);
        assertThat(result.getProfileUri()).isEqualTo(profileUri);
        assertThat(result.getName()).isEqualTo(name);
        assertThat(result.isReceiveEmails()).isEqualTo(receiveEmails);
    }

    @Test
    @DisplayName("중복된 이메일로 멤버를 등록할 수 없다")
    void testRegisterFailDuplicatedEmail() throws Exception {
        // given
        String duplicatedEmail = "a@naver.com";
        Member member = Member.builder()
            .email(duplicatedEmail)
            .build();
        em.persist(member);

        MemberRegisterRequest request = MemberRegisterRequest.builder()
            .email(duplicatedEmail)
            .build();
        // when & then
        assertThatThrownBy(() -> memberService.register(request))
            .isInstanceOf(ApiException.class)
            .hasMessageContaining(ErrorCode.MEMBER_ALREADY_REGISTERED.getMessage());
    }

    @Test
    @DisplayName("멤버를 조회한다")
    void testFindMember() throws Exception {
        // given
        Member member = Member.builder()
            .email("a@naver.com")
            .profileUri("https://imageurl")
            .name("태훈")
            .role(Role.USER)
            .build();
        em.persist(member);

        // when
        MemberFindResponse response = memberService.find(member.getId());

        // then
        assertThat(response.getId()).isEqualTo(member.getId());
        assertThat(response.getEmail()).isEqualTo(member.getEmail());
        assertThat(response.getProfileUri()).isEqualTo(member.getProfileUri());
        assertThat(response.getName()).isEqualTo(member.getName());
    }

    @Test
    @DisplayName("존재하지 않는 멤버는 조회할 수 없다")
    void testFindMemberFailMemberNotFound() throws Exception {
        Long notRegisteredMemberId = 1232145L;

        // when & then
        assertThatThrownBy(() -> memberService.find(notRegisteredMemberId))
            .isInstanceOf(ApiException.class)
            .hasMessageContaining(ErrorCode.MEMBER_NOT_FOUND.getMessage());
    }

    @Test
    @DisplayName("사이트의 어드민은 조회할 수 없다")
    void testFindMemberFailMemberIsAdmin() throws Exception {
        // given
        Member member = Member.builder()
            .email("a@naver.com")
            .profileUri("https://imageurl")
            .name("태훈")
            .role(Role.ADMIN)
            .build();
        em.persist(member);

        // when & then
        assertThatThrownBy(() -> memberService.find(member.getId()))
            .isInstanceOf(ApiException.class)
            .hasMessageContaining(ErrorCode.MEMBER_NOT_FOUND.getMessage());
    }
}
