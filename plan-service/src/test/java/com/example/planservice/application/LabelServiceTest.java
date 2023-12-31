package com.example.planservice.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import com.example.planservice.application.dto.LabelDeleteServiceRequest;
import com.example.planservice.domain.label.Label;
import com.example.planservice.domain.member.Member;
import com.example.planservice.domain.memberofplan.MemberOfPlan;
import com.example.planservice.domain.plan.Plan;
import com.example.planservice.exception.ApiException;
import com.example.planservice.exception.ErrorCode;
import com.example.planservice.presentation.dto.request.LabelCreateRequest;
import com.example.planservice.presentation.dto.response.LabelFindResponse;
import jakarta.persistence.EntityManager;

@SpringBootTest
@Transactional
class LabelServiceTest {
    @Autowired
    LabelService labelService;

    @Autowired
    EntityManager em;

    @Test
    @DisplayName("라벨을 생성한다")
    void testCreateLabel() throws Exception {
        // given
        String name = "라벨1";
        Plan plan = createPlanUsingTest();
        Member member = createMemberUsingTest(plan);

        LabelCreateRequest request = LabelCreateRequest.builder()
            .planId(plan.getId())
            .name(name)
            .build();

        // when
        Long createdId = labelService.create(member.getId(), request);

        // then
        Label result = em.find(Label.class, createdId);
        assertThat(result.getName()).isEqualTo(name);
        assertThat(plan.getLabels()).hasSize(1)
            .containsExactly(result);
    }

    @Test
    @DisplayName("라벨에는 플랜이 포함되어야 한다")
    void testCreateLabelFailPlanNotFound() throws Exception {
        // given
        String name = "라벨1";
        Member member = createMemberUsingTest(null);

        LabelCreateRequest request = LabelCreateRequest.builder()
            .planId(1231412L)
            .name(name)
            .build();

        // when & then
        assertThatThrownBy(() -> labelService.create(member.getId(), request))
            .isInstanceOf(ApiException.class)
            .hasMessageContaining(ErrorCode.PLAN_NOT_FOUND.getMessage());
    }

    @Test
    @DisplayName("플랜에 소속된 멤버만 라벨을 생성할 수 있다")
    void testCreateLabelFailMemberNotExistPlan() throws Exception {
        // given
        String name = "라벨1";
        Plan plan = createPlanUsingTest();
        Member member = createMemberUsingTest(null);

        LabelCreateRequest request = LabelCreateRequest.builder()
            .planId(plan.getId())
            .name(name)
            .build();

        // when & then
        assertThatThrownBy(() -> labelService.create(member.getId(), request))
            .isInstanceOf(ApiException.class)
            .hasMessageContaining(ErrorCode.MEMBER_NOT_FOUND_IN_PLAN.getMessage());
    }

    @Test
    @DisplayName("하나의 플랜 안에서는 동일한 이름의 라벨이 등록될 수 없다")
    void testCreateLabelFailNameDuplicated() throws Exception {
        // given
        String duplicatedName = "중복이름";
        Plan plan = createPlanUsingTest();
        Member member = createMemberUsingTest(plan);
        createLabelUsingTest(duplicatedName, plan);

        LabelCreateRequest request = LabelCreateRequest.builder()
            .planId(plan.getId())
            .name(duplicatedName)
            .build();

        // when & then
        assertThatThrownBy(() -> labelService.create(member.getId(), request))
            .isInstanceOf(ApiException.class)
            .hasMessageContaining(ErrorCode.LABEL_NAME_DUPLICATE.getMessage());
    }

    @Test
    @DisplayName("라벨을 삭제한다")
    void testDeleteLabel() throws Exception {
        // given
        Plan plan = createPlanUsingTest();
        Member member = createMemberUsingTest(plan);
        Label label = createLabelUsingTest("라벨명", plan);

        LabelDeleteServiceRequest request = LabelDeleteServiceRequest.builder()
            .labelId(label.getId())
            .memberId(member.getId())
            .planId(plan.getId())
            .build();

        // when
        labelService.delete(request);

        // then
        Label result = em.find(Label.class, label.getId());
        assertThat(result).isNull();
        assertThat(plan.getLabels()).doesNotContain(label);
    }

    @Test
    @DisplayName("플랜에 소속된 멤버만 라벨을 삭제할 수 있다")
    void testDeleteLabelFailNoAuthorize() throws Exception {
        // given
        Plan plan = createPlanUsingTest();
        Plan otherPlan = createPlanUsingTest();
        Member member = createMemberUsingTest(otherPlan);
        Label label = createLabelUsingTest("라벨명", plan);

        LabelDeleteServiceRequest request = LabelDeleteServiceRequest.builder()
            .planId(plan.getId())
            .memberId(member.getId())
            .labelId(label.getId())
            .build();

        // when & then
        assertThatThrownBy(() -> labelService.delete(request))
            .isInstanceOf(ApiException.class)
            .hasMessageContaining(ErrorCode.MEMBER_NOT_FOUND_IN_PLAN.getMessage());
    }

    @Test
    @DisplayName("라벨 삭제 시 입력받은 PlanId는 라벨의 Plan과 일치해야 한다")
    void testDeleteLabelFailNotSamePlan() throws Exception {
        // given
        Plan plan = createPlanUsingTest();
        Member member = createMemberUsingTest(plan);

        Plan otherPlan = createPlanUsingTest();
        Label label = createLabelUsingTest("라벨명", otherPlan);

        LabelDeleteServiceRequest request = LabelDeleteServiceRequest.builder()
            .planId(plan.getId())
            .memberId(member.getId())
            .labelId(label.getId())
            .build();

        // when & then
        assertThatThrownBy(() -> labelService.delete(request))
            .isInstanceOf(ApiException.class)
            .hasMessageContaining(ErrorCode.AUTHORIZATION_FAIL.getMessage());
    }

    @Test
    @DisplayName("존재하지 않는 라벨을 삭제할 수 없다")
    void testDeleteLabelFailNotExists() throws Exception {
        // given
        Plan plan = createPlanUsingTest();
        Member member = createMemberUsingTest(plan);

        LabelDeleteServiceRequest request = LabelDeleteServiceRequest.builder()
            .planId(plan.getId())
            .memberId(member.getId())
            .labelId(12312312L)
            .build();

        // when & then
        assertThatThrownBy(() -> labelService.delete(request))
            .isInstanceOf(ApiException.class)
            .hasMessageContaining(ErrorCode.LABEL_NOT_FOUND.getMessage());
    }

    @Test
    @DisplayName("라벨이 소속된 플랜이 Private일 때, 해당 플랜에 소속된 사용자들은 조회가 가능하다")
    void testFindLabel() throws Exception {
        // given
        Plan plan = createPrivatePlanUsingTest();
        Member member = createMemberUsingTest(plan);
        Label label = createLabelUsingTest("라벨", plan);

        // when
        LabelFindResponse response = labelService.find(label.getId(), member.getId());

        // then
        assertThat(response.getId()).isEqualTo(label.getId());
        assertThat(response.getPlanId()).isEqualTo(plan.getId());
        assertThat(response.getName()).isEqualTo(label.getName());
    }

    @Test
    @DisplayName("라벨이 소속된 플랜이 Public이면 로그인한 누구나 조회가 가능하다")
    void testFindLabelAboutPublicPlanOnLoginMember() throws Exception {
        // given
        Plan plan = createPlanUsingTest();
        Label label = createLabelUsingTest("라벨", plan);
        Member memberInOtherPlan = createMemberUsingTest(null);

        // when
        LabelFindResponse response = labelService.find(label.getId(), memberInOtherPlan.getId());

        // then
        assertThat(response.getId()).isEqualTo(label.getId());
        assertThat(response.getPlanId()).isEqualTo(plan.getId());
        assertThat(response.getName()).isEqualTo(label.getName());
    }

    @Test
    @DisplayName("라벨이 속해있는 플랜이 Private일 경우 플랜에 소속되지 않은 사용자는 라벨을 확인할 수 없다")
    void testFindLabelFailNoAuthorized() throws Exception {
        // given
        Plan plan = createPrivatePlanUsingTest();
        Plan otherPlan = createPrivatePlanUsingTest();
        Member memberInOtherPlan = createMemberUsingTest(otherPlan);
        Label label = createLabelUsingTest("라벨임", plan);

        // when & then
        assertThatThrownBy(() -> labelService.find(label.getId(), memberInOtherPlan.getId()))
            .isInstanceOf(ApiException.class)
            .hasMessageContaining(ErrorCode.MEMBER_NOT_FOUND_IN_PLAN.getMessage());
    }

    @Test
    @DisplayName("존재하지 않는 라벨을 조회할 수 없다")
    void testFindLabelFailLabelNotFound() throws Exception {
        // given
        Plan plan = createPlanUsingTest();
        Member member = createMemberUsingTest(plan);
        Long notRegisteredLabelId = 12321541L;

        // when & then
        assertThatThrownBy(() -> labelService.find(notRegisteredLabelId, member.getId()))
            .isInstanceOf(ApiException.class)
            .hasMessageContaining(ErrorCode.LABEL_NOT_FOUND.getMessage());
    }


    private Label createLabelUsingTest(String name, Plan plan) {
        Label label = Label.create(name, plan);
        em.persist(label);
        return label;
    }

    private Member createMemberUsingTest(Plan plan) {
        Member member = Member.builder()
            .build();
        em.persist(member);
        if (plan == null) {
            return member;
        }
        MemberOfPlan memberOfPlan = MemberOfPlan.builder()
            .plan(plan)
            .member(member)
            .build();
        em.persist(memberOfPlan);
        return member;
    }

    private Plan createPlanUsingTest() {
        Plan plan = Plan.builder()
            .isPublic(true)
            .build();
        em.persist(plan);
        return plan;
    }

    private Plan createPrivatePlanUsingTest() {
        Plan plan = Plan.builder()
            .isPublic(false)
            .build();
        em.persist(plan);
        return plan;
    }

}
