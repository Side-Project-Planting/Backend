package com.example.planservice.domain.memberofplan.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import com.example.planservice.domain.member.Member;
import com.example.planservice.domain.member.repository.MemberRepository;
import com.example.planservice.domain.memberofplan.MemberOfPlan;
import com.example.planservice.domain.plan.Plan;
import com.example.planservice.domain.plan.repository.PlanRepository;

@SpringBootTest
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
        Member member = Member.builder()
            .build();
        memberRepository.save(member);
        Plan plan = Plan.builder()
            .build();
        planRepository.save(plan);
        MemberOfPlan memberOfPlan = MemberOfPlan.builder()
            .member(member)
            .plan(plan)
            .build();
        memberOfPlanRepository.save(memberOfPlan);

        // when
        MemberOfPlan result =
            memberOfPlanRepository.findByPlanIdAndMemberId(plan.getId(), member.getId())
                .get();

        // then
        assertThat(result.getMember()).isEqualTo(member);
        assertThat(result.getPlan()).isEqualTo(plan);
    }

    @Test
    @DisplayName("planId와 memberId로 해당되는 MemberOfPlan 엔티티가 없으면 Optional.empty()를 반환한다")
    void testFindByPlanIdAndMemberIdFail() {
        // given
        Member member = Member.builder()
            .build();
        memberRepository.save(member);
        Plan plan = Plan.builder()
            .build();
        planRepository.save(plan);

        // when
        Optional<MemberOfPlan> memberOfPlanOpt =
            memberOfPlanRepository.findByPlanIdAndMemberId(plan.getId(), member.getId());


        // then
        assertThat(memberOfPlanOpt).isEmpty();
    }

    @Test
    @DisplayName("planId와 여러개의 memberId로 해당되는 MemberOfPlan 엔티티를 삭제한다")
    void deleteAllByPlanIdAndMemberIdsTest() {
        // Given
        Member member1 = Member.createNormalUser()
            .profileUri("profile1")
            .name("name1")
            .email("email1")
            .receiveEmails(true)
            .build();
        Member member2 = Member.createNormalUser()
            .profileUri("profile2")
            .name("name2")
            .email("email2")
            .receiveEmails(true)
            .build();
        Member member3 = Member.createNormalUser()
            .profileUri("profile3")
            .name("name3")
            .email("email3")
            .receiveEmails(true)
            .build();

        memberRepository.saveAll(List.of(member1, member2, member3));

        Plan plan = Plan.builder()
            .owner(member1)
            .title("title")
            .intro("intro")
            .isPublic(true)
            .starCnt(0)
            .viewCnt(0)
            .isDeleted(false)
            .build();

        plan = planRepository.save(plan);

        MemberOfPlan memberOfPlan1 = MemberOfPlan.builder()
            .plan(plan)
            .member(member2)
            .build();

        MemberOfPlan memberOfPlan2 = MemberOfPlan.builder()
            .plan(plan)
            .member(member3)
            .build();

        memberOfPlanRepository.saveAll(List.of(memberOfPlan1, memberOfPlan2));

        // When
        memberOfPlanRepository.deleteAllByPlanIdAndMemberIds(plan.getId(),
            List.of(member2.getId(), member3.getId()));

        // Then
        List<MemberOfPlan> membersOfPlan = memberOfPlanRepository.findAllByPlanId(plan.getId())
            .get();
        assertEquals(0, membersOfPlan.size());
    }
}
