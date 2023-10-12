package com.example.planservice.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;

import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import com.example.planservice.domain.member.Member;
import com.example.planservice.domain.member.repository.MemberRepository;
import com.example.planservice.domain.memberofplan.MemberOfPlan;
import com.example.planservice.domain.memberofplan.repository.MemberOfPlanRepository;
import com.example.planservice.domain.plan.Plan;
import com.example.planservice.domain.plan.repository.PlanRepository;
import com.example.planservice.domain.tab.Tab;
import com.example.planservice.domain.tab.repository.TabRepository;
import com.example.planservice.exception.ApiException;
import com.example.planservice.exception.ErrorCode;
import com.example.planservice.presentation.dto.request.TabCreateRequest;

@SpringBootTest
@Transactional
class TabServiceTest {
    @Autowired
    TabService tabService;

    @Autowired
    TabRepository tabRepository;

    @Autowired
    PlanRepository planRepository;

    @Autowired
    MemberOfPlanRepository memberOfPlanRepository;

    @Autowired
    MemberRepository memberRepository;

    @Test
    @DisplayName("탭을 생성한다")
    void create() {
        // given
        Plan plan = createPlan();
        Member member = createMember();
        createMemberOfPlan(plan, member);

        TabCreateRequest request = createTabCreateRequest(plan.getId(), "새로운탭");

        // when
        Long savedId = tabService.create(member.getId(), request);

        // then
        assertThat(savedId).isNotNull();

        Tab savedTab = tabRepository.findById(savedId).get();
        assertThat(savedTab.getName()).isEqualTo(request.getName());
        assertThat(savedTab.getPlan().getId()).isEqualTo(request.getPlanId());
    }


    @Test
    @DisplayName("탭은 존재하는 플랜에 대해서만 생성할 수 있다")
    void createFailNotExistPlan() {
        // given
        Long userId = 1L;
        Long notRegisteredPlanId = 10L;
        TabCreateRequest request = createTabCreateRequest(notRegisteredPlanId, "탭이름");

        // when & then
        assertThatThrownBy(() -> tabService.create(userId, request))
            .isInstanceOf(ApiException.class)
            .hasMessageContaining(ErrorCode.PLAN_NOT_FOUND.getMessage());
    }

    @Test
    @DisplayName("탭을 생성하는 사람은 해당 플랜에 소속되어 있어야 한다")
    void createFailNotMemberOfPlan() {
        // given
        Plan plan = createPlan();

        Long userId = 1L;
        TabCreateRequest request = createTabCreateRequest(plan.getId(), "이름");

        // when & then
        assertThatThrownBy(() -> tabService.create(userId, request))
            .isInstanceOf(ApiException.class)
            .hasMessageContaining(ErrorCode.MEMBER_NOT_FOUND_IN_PLAN.getMessage());
    }

    @Test
    @DisplayName("하나의 플랜에 탭은 최대 5개까지 생성 가능하다")
    @SuppressWarnings("squid:S5778")
    void createFailTabLimitOver() {
        // given
        Plan plan = createPlan();
        Member member = createMember();
        createMemberOfPlan(plan, member);

        Tab tab1 = Tab.builder().plan(plan).build();
        Tab tab2 = Tab.builder().plan(plan).build();
        Tab tab3 = Tab.builder().plan(plan).build();
        Tab tab4 = Tab.builder().plan(plan).build();
        Tab tab5 = Tab.builder().plan(plan).build();
        tabRepository.saveAll(List.of(tab1, tab2, tab3, tab4, tab5));

        TabCreateRequest request = createTabCreateRequest(plan.getId(), "이름");

        // when & then
        assertThatThrownBy(() -> tabService.create(member.getId(), request))
            .isInstanceOf(ApiException.class)
            .hasMessageContaining(ErrorCode.TAB_SIZE_LIMIT.getMessage());
    }

    @Test
    @DisplayName("탭이 4개인 경우 생성에 성공한다")
    void createSuccessTabSizeNotOver() {
        // given
        Plan plan = createPlan();
        Member member = createMember();
        createMemberOfPlan(plan, member);

        Tab tab1 = Tab.builder().plan(plan).build();
        Tab tab2 = Tab.builder().plan(plan).build();
        Tab tab3 = Tab.builder().plan(plan).build();
        Tab tab4 = Tab.builder().plan(plan).build();
        tabRepository.saveAll(List.of(tab1, tab2, tab3, tab4));

        TabCreateRequest request = createTabCreateRequest(plan.getId(), "이름");

        // when
        Long savedId = tabService.create(member.getId(), request);

        // then
        assertThat(savedId).isNotNull();

        Tab savedTab = tabRepository.findById(savedId).get();
        assertThat(savedTab.getName()).isEqualTo(request.getName());
        assertThat(savedTab.getPlan().getId()).isEqualTo(request.getPlanId());
    }

    @Test
    @DisplayName("동일한 플랜에서 탭 이름은 중복될 수 없다")
    @SuppressWarnings("squid:S5778")
    void createFailDuplicatedTabName() {
        // given
        String tabName = "탭이름";
        Plan plan = createPlan();
        Member member = createMember();
        createMemberOfPlan(plan, member);
        createTab(plan, tabName, null);

        TabCreateRequest request = createTabCreateRequest(plan.getId(), tabName);

        // when & then
        assertThatThrownBy(() -> tabService.create(member.getId(), request))
            .isInstanceOf(ApiException.class)
            .hasMessageContaining(ErrorCode.TAB_NAME_DUPLICATE.getMessage());
    }

    @Test
    @DisplayName("새롭게 생성된 탭은 가장 마지막 순서를 갖는다")
    void isLastSequenceAboutCreatedTab() {
        // given
        Plan plan = createPlan();
        Member member = createMember();
        createMemberOfPlan(plan, member);

        Tab tab2 = createTab(plan, "이전탭2", null);
        Tab tab1 = createTab(plan, "이전탭1", tab2);
        tabRepository.saveAll(List.of(tab1, tab2));

        TabCreateRequest request = createTabCreateRequest(plan.getId(), "탭이름");

        // when
        Long savedId = tabService.create(member.getId(), request);

        // then
        assertThat(savedId).isNotNull();

        Tab savedTab = tabRepository.findById(savedId).get();
        assertThat(tab2.getNext()).isEqualTo(savedTab);
        assertThat(savedTab.getNext()).isNull();
    }

    private Tab createTab(Plan plan, String name, Tab next) {
        Tab tab = Tab.builder()
            .plan(plan)
            .name(name)
            .next(next)
            .build();
        tabRepository.save(tab);
        return tab;
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

    @NotNull
    private TabCreateRequest createTabCreateRequest(Long planId, String name) {
        return TabCreateRequest.builder()
            .name(name)
            .planId(planId)
            .build();
    }
}