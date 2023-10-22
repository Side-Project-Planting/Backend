package com.example.planservice.domain.memberofplan.repository;

import com.example.planservice.domain.member.Member;
import com.example.planservice.domain.member.repository.MemberRepository;
import com.example.planservice.domain.memberofplan.MemberOfPlan;
import com.example.planservice.domain.plan.Plan;
import com.example.planservice.domain.plan.repository.PlanRepository;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class MemberOfPlanRepositoryTest {
    @Autowired
    MemberOfPlanRepository memberOfPlanRepository;

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    PlanRepository planRepository;

    @Test
    @DisplayName("플랜에 유저가 존재하면 true를 반환한다")
    void existsByPlanIdAndMemberIdTrue() {
        // given
        Member member = Member.builder().build();
        memberRepository.save(member);
        Plan plan = Plan.builder().build();
        planRepository.save(plan);
        MemberOfPlan memberOfPlan = MemberOfPlan.builder()
            .member(member)
            .plan(plan)
            .build();
        memberOfPlanRepository.save(memberOfPlan);

        // when
        boolean exists = memberOfPlanRepository.existsByPlanIdAndMemberId(plan.getId(), member.getId());

        // then
        assertThat(exists).isTrue();
    }

    @Test
    @DisplayName("플랜에 유저가 없으면 false를 반환한다")
    void existsByPlanIdAndMemberIdFail() {
        // given
        Long planId = 1L;
        Long memberId = 10L;

        // when
        boolean exists = memberOfPlanRepository.existsByPlanIdAndMemberId(planId, memberId);

        // then
        assertThat(exists).isFalse();
    }

}