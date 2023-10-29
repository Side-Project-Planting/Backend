package com.example.planservice.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.transaction.annotation.Transactional;

import com.example.planservice.domain.member.Member;
import com.example.planservice.domain.member.repository.MemberRepository;
import com.example.planservice.domain.plan.Plan;
import com.example.planservice.domain.plan.repository.PlanRepository;
import com.example.planservice.exception.ApiException;
import com.example.planservice.exception.ErrorCode;
import com.example.planservice.presentation.dto.request.PlanCreateRequest;

@SpringBootTest
@Transactional
class PlanServiceTest {
    @Autowired
    PlanService planService;

    @MockBean
    EmailService emailService;

    @Autowired
    PlanRepository planRepository;

    @Autowired
    MemberRepository memberRepository;
    private Long userId;

    @BeforeEach
    void testSetUp() {
        Member member = Member.builder().name("tester").email("test@example.com").build();
        Member savedMember = memberRepository.save(member);
        userId = savedMember.getId();

        Mockito.doNothing().when(emailService).sendEmail(ArgumentMatchers.anyString(),
            ArgumentMatchers.anyString());
    }

    @Test
    @DisplayName("플랜을 생성한다")
    void create() {
        // given
        List<String> invitedEmails = List.of("test@example.com");

        PlanCreateRequest request = PlanCreateRequest.builder()
            .title("플랜 제목")
            .intro("플랜 소개")
            .isPublic(true)
            .invitedEmails(invitedEmails)
            .build();

        // when
        Long savedId = planService.create(request, userId);

        // then
        assertThat(savedId).isNotNull();

        Plan savedPlan = planRepository.findById(savedId).get();
        assertThat(savedPlan.getTitle()).isEqualTo(request.getTitle());
        assertThat(savedPlan.getIntro()).isEqualTo(request.getIntro());
    }

    @Test
    @DisplayName("존재하지 않는 사용자로 플랜을 생성하려고 하면 실패한다")
    void createFailNotExistUser() {
        // given
        Long notRegisteredUserId = 10L;
        List<String> invitedEmails = List.of("test@example.com");

        PlanCreateRequest request = PlanCreateRequest.builder()
            .title("플랜 제목")
            .intro("플랜 소개")
            .isPublic(true)
            .invitedEmails(invitedEmails)
            .build();

        // when & then
        assertThatThrownBy(() -> planService.create(request, notRegisteredUserId))
            .isInstanceOf(ApiException.class)
            .hasMessageContaining(ErrorCode.MEMBER_NOT_FOUND.getMessage());
    }
}
