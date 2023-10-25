package com.example.planservice.domain.memberofplan.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import com.example.planservice.domain.member.Member;
import com.example.planservice.domain.member.repository.MemberRepository;
import com.example.planservice.domain.memberofplan.MemberOfPlan;
import com.example.planservice.domain.plan.Plan;
import com.example.planservice.domain.plan.repository.PlanRepository;

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
    @DisplayName("planId와 memberId로 해당되는 MemberOfPlan 인스턴스를 찾는다")
    void testFindByPlanIdAndMemberId() {
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
        MemberOfPlan result =
            memberOfPlanRepository.findByPlanIdAndMemberId(plan.getId(), member.getId()).get();

        // then
        assertThat(result.getMember()).isEqualTo(member);
        assertThat(result.getPlan()).isEqualTo(plan);
    }

    @Test
    @DisplayName("planId와 memberId로 해당되는 MemberOfPlan 엔티티가 없으면 Optional.empty()를 반환한다")
    void testFindByPlanIdAndMemberIdFail() {
        // given
        Member member = Member.builder().build();
        memberRepository.save(member);
        Plan plan = Plan.builder().build();
        planRepository.save(plan);

        // when
        Optional<MemberOfPlan> memberOfPlanOpt =
            memberOfPlanRepository.findByPlanIdAndMemberId(plan.getId(), member.getId());


        // then
        assertThat(memberOfPlanOpt).isEmpty();
    }

}