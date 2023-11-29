package com.example.planservice.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.Transactional;

import com.example.planservice.config.TestConfig;
import com.example.planservice.domain.member.Member;
import com.example.planservice.domain.member.repository.MemberRepository;
import com.example.planservice.domain.memberofplan.MemberOfPlan;
import com.example.planservice.domain.memberofplan.repository.MemberOfPlanRepository;
import com.example.planservice.domain.plan.Plan;
import com.example.planservice.domain.plan.repository.PlanRepository;
import com.example.planservice.exception.ApiException;
import com.example.planservice.exception.ErrorCode;

@SpringBootTest
@Import(TestConfig.class)
@Transactional
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
    @DisplayName("플랜에 소속된 멤버는 플랜에 접근할 수 있다")
    void testGetPlanAfterValidateAuthorization() {
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
    @DisplayName("존재하지 않는 플랜에는 접근할 수 없다")
    void testGetPlanAfterValidateAuthorizationFailNotFound() {
        // given
        Member member = createMember();
        long notRegisteredPlanId = 123123L;
        // when & then
        assertThatThrownBy(() -> service.getPlanAfterValidateAuthorization(notRegisteredPlanId, member.getId()))
            .isInstanceOf(ApiException.class)
            .hasMessageContaining(ErrorCode.PLAN_NOT_FOUND.getMessage());
    }

    @Test
    @DisplayName("플랜에 소속되어 있지 않는 멤버는 해당 플랜에 접근이 불가능하다")
    void testGetPlanAfterValidateAuthorizationFailNotAuthorization() {
        // given
        Plan plan = createPlan();
        Member member = createMember();

        // when & then
        assertThatThrownBy(() -> service.getPlanAfterValidateAuthorization(plan.getId(), member.getId()))
            .isInstanceOf(ApiException.class)
            .hasMessageContaining(ErrorCode.MEMBER_NOT_FOUND_IN_PLAN.getMessage());
    }

    @Test
    @DisplayName("플랜의 소유주를 검사한다")
    void testValidatePlanOwner() {
        // given
        Member member = createMember();
        Plan plan = Plan.builder()
            .owner(member)
            .build();
        planRepository.save(plan);

        // when
        boolean result = service.validatePlanOwner(plan.getId(), member.getId());

        // then
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("플랜의 소유주가 아니면 false를 반환한다")
    void validateOwnerFail() {
        // given
        Member member = createMember();
        Member otherMember = createMember();
        Plan plan = Plan.builder()
            .owner(member)
            .build();
        planRepository.save(plan);

        // when
        boolean result = service.validatePlanOwner(plan.getId(), otherMember.getId());

        // then
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("플랜에 소속된 멤버는 MemberOfPlan을 가져올 수 있다")
    void testValidateMemberIsInThePlan() throws Exception {
        // given
        Plan plan = createPlan();
        Member member = createMember();
        createMemberOfPlan(plan, member);

        // when
        MemberOfPlan memberOfPlan = service.validateMemberIsInThePlan(member.getId(), plan);

        // then
        assertThat(memberOfPlan.getMember()).isEqualTo(member);
        assertThat(memberOfPlan.getPlan()).isEqualTo(plan);
    }

    @Test
    @DisplayName("플랜에 소속되지 않은 멤버는 MemberOfPlan을 가져올 수 없다")
    void testValidateMemberIsInThePlanFailNotFoundMember() throws Exception {
        // given
        Plan plan = createPlan();
        Member otherPlanMember = createMember();

        // when & then
        assertThatThrownBy(() -> service.validateMemberIsInThePlan(otherPlanMember.getId(), plan))
            .isInstanceOf(ApiException.class)
            .hasMessageContaining(ErrorCode.MEMBER_NOT_FOUND_IN_PLAN.getMessage());
    }

    @Test
    @DisplayName("플랜에 소속된 멤버를 가져온다")
    void testGetMemberBelongingToPlan() throws Exception {
        // given
        Plan plan = createPlan();
        Member member = createMember();
        createMemberOfPlan(plan, member);

        // when
        Member memberFounded = service.getMemberBelongingToPlan(member.getId(), plan);

        // then
        assertThat(memberFounded).isEqualTo(member);
    }

    @Test
    @DisplayName("플랜에 소속되지 않은 멤버는 가져올 수 없다")
    void testGetMemberBelongingToPlanFailNotFoundMember() throws Exception {
        // given
        Plan plan = createPlan();
        Member otherPlanMember = createMember();

        // when & then
        assertThatThrownBy(() -> service.getMemberBelongingToPlan(otherPlanMember.getId(), plan))
            .isInstanceOf(ApiException.class)
            .hasMessageContaining(ErrorCode.MEMBER_NOT_FOUND_IN_PLAN.getMessage());
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
        Plan plan = Plan.builder()
            .build();
        planRepository.save(plan);
        return plan;
    }

}
