package com.example.planservice.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import com.example.planservice.domain.member.Member;
import com.example.planservice.domain.member.repository.MemberRepository;
import com.example.planservice.domain.memberofplan.MemberOfPlan;
import com.example.planservice.domain.memberofplan.repository.MemberOfPlanRepository;
import com.example.planservice.domain.plan.Plan;
import com.example.planservice.domain.plan.repository.PlanRepository;
import com.example.planservice.exception.ApiException;
import com.example.planservice.exception.ErrorCode;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
@SuppressWarnings("squid:S5778")
class PlanMembershipServiceTest {
    @Autowired
    PlanMembershipService service;

    @Autowired
    PlanRepository planRepository;

    @Autowired
    MemberOfPlanRepository memberOfPlanRepository;

    @Autowired
    MemberRepository memberRepository;

    @Test
    @DisplayName("멤버가 플랜에 소속되어 있으면 정상적으로 플랜을 반환한다")
    void verifySuccess() {
        // given
        Plan plan = createPlan();
        Member member = createMember();
        createMemberOfPlan(plan, member);

        // when
        Plan result = service.getPlanAfterValidateAuthorization(plan.getId(), member.getId());

        // then
        assertThat(result.getId()).isEqualTo(plan.getId());
    }

    @Test
    @DisplayName("존재하지 않는 플랜에 대해서는 예외를 반환한다")
    void verifyFailPlanNotFound() {
        // given
        Member member = createMember();

        // when & then
        assertThatThrownBy(() -> service.getPlanAfterValidateAuthorization(123123L, member.getId()))
            .isInstanceOf(ApiException.class)
            .hasMessageContaining(ErrorCode.PLAN_NOT_FOUND.getMessage());
    }

    @Test
    @DisplayName("플랜에 소속되어 있지 않는 멤버에 대해서는 예외를 반환한다")
    void verifyFailMemberNotExistInPlan() {
        // given
        Plan plan = createPlan();
        Member member = createMember();

        // when & then
        assertThatThrownBy(() -> service.getPlanAfterValidateAuthorization(plan.getId(), member.getId()))
            .isInstanceOf(ApiException.class)
            .hasMessageContaining(ErrorCode.MEMBER_NOT_FOUND_IN_PLAN.getMessage());
    }

    @Test
    @DisplayName("해당 플랜의 소유주가 맞으면 validate 결과로 true를 반환한다")
    void validateOwner() {
        // given
        Member member = createMember();
        Plan plan = Plan.builder().owner(member).build();
        planRepository.save(plan);

        // when
        boolean result = service.validatePlanOwner(plan.getId(), member.getId());

        // then
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("해당 플랜의 소유주가 아니면 validate 결과로 false를 반환한다")
    void validateOwnerFail() {
        // given
        Member member = createMember();
        Member otherMember = createMember();
        Plan plan = Plan.builder().owner(member).build();
        planRepository.save(plan);

        // when
        boolean result = service.validatePlanOwner(plan.getId(), otherMember.getId());

        // then
        assertThat(result).isFalse();
    }

    @NotNull
    private MemberOfPlan createMemberOfPlan(Plan plan, Member member) {
        MemberOfPlan memberOfPlan = MemberOfPlan.builder()
            .plan(plan)
            .member(member)
            .build();
        memberOfPlanRepository.save(memberOfPlan);
        return memberOfPlan;
    }

    @NotNull
    private Member createMember() {
        Member member = Member.builder()
            .build();
        memberRepository.save(member);
        return member;
    }

    @NotNull
    private Plan createPlan() {
        Plan plan = Plan.builder().build();
        planRepository.save(plan);
        return plan;
    }

}