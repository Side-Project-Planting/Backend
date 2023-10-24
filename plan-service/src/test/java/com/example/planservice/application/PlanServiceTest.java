package com.example.planservice.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

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

    @Autowired
    PlanRepository planRepository;

    @Autowired
    MemberRepository memberRepository;

    @Test
    @DisplayName("플랜을 생성한다")
    void create() {
        // given
        final Long userId = 1L; // Make sure this user exists in your test database.
        final List<String> invitedEmails = List.of("test@example.com");

        final PlanCreateRequest request = PlanCreateRequest.builder()
                                                           .title("플랜 제목")
                                                           .intro("플랜 소개")
                                                           .isPublic(true)
                                                           .invitedEmails(invitedEmails)
                                                           .build();

        // when
        final Long savedId = planService.create(request, userId);

        // then
        assertThat(savedId).isNotNull();

        final Plan savedPlan = planRepository.findById(savedId).get();
        assertThat(savedPlan.getTitle()).isEqualTo(request.getTitle());
        assertThat(savedPlan.getIntro()).isEqualTo(request.getIntro());
    }

    @Test
    @DisplayName("존재하지 않는 사용자로 플랜을 생성하려고 하면 실패한다")
    void createFailNotExistUser() {
        // given
        final Long notRegisteredUserId = 10L;
        final List<String> invitedEmails = List.of("test@example.com");

        final PlanCreateRequest request = PlanCreateRequest.builder()
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
