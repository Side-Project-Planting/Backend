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

import com.example.planservice.application.dto.TabChangeTitleResponse;
import com.example.planservice.application.dto.TabChangeTitleServiceRequest;
import com.example.planservice.application.dto.TabDeleteServiceRequest;
import com.example.planservice.domain.member.Member;
import com.example.planservice.domain.memberofplan.MemberOfPlan;
import com.example.planservice.domain.plan.Plan;
import com.example.planservice.domain.tab.Tab;
import com.example.planservice.domain.tab.repository.TabRepository;
import com.example.planservice.domain.task.Task;
import com.example.planservice.domain.task.repository.TaskRepository;
import com.example.planservice.exception.ApiException;
import com.example.planservice.exception.ErrorCode;
import com.example.planservice.presentation.dto.request.TabChangeOrderRequest;
import com.example.planservice.presentation.dto.request.TabCreateRequest;
import com.example.planservice.presentation.dto.response.TabFindResponse;
import jakarta.persistence.EntityManager;

@SpringBootTest
@Transactional
class TabServiceTest {
    @Autowired
    TabService tabService;

    @Autowired
    TabRepository tabRepository;

    @Autowired
    TaskRepository taskRepository;

    @Autowired
    EntityManager em;

    @Test
    @DisplayName("탭을 생성한다")
    void create() {
        // given
        Plan plan = createPlan();
        Member member = createMember();
        createMemberOfPlan(plan, member);
        createTab(plan, "TODO", null, true);

        TabCreateRequest request = createTabCreateRequest(plan.getId(), "새로운탭");

        // when
        Long savedId = tabService.create(member.getId(), request);

        // then
        assertThat(savedId).isNotNull();

        Tab savedTab = tabRepository.findById(savedId)
            .get();
        assertThat(savedTab)
            .extracting(Tab::getTitle, Tab::getPlan)
            .containsExactly(request.getTitle(), plan);

        Task firstDummyTask = savedTab.getFirstDummyTask();
        Task lastDummyTask = savedTab.getLastDummyTask();
        assertThat(firstDummyTask.getTitle()).isEqualTo("first");
        assertThat(lastDummyTask.getTitle()).isEqualTo("last");
        assertThat(savedTab.getTasks()).hasSize(2)
            .contains(firstDummyTask, lastDummyTask);
    }

    @Test
    @DisplayName("플랜을 기준으로 두 번째 탭부터는 생성시 isFirst 속성이 false이다")
    void checkSecondTabIsNotFirst() {
        // given
        Plan plan = createPlan();
        Member member = createMember();
        createMemberOfPlan(plan, member);
        Tab oldTab = createTab(plan, "과거탭", null, true);

        TabCreateRequest request = createTabCreateRequest(plan.getId(), "새로운탭");

        // when
        Long savedId = tabService.create(member.getId(), request);

        // then
        Tab newTab = tabRepository.findById(savedId)
            .get();
        assertThat(newTab.isFirst()).isFalse();
        assertThat(oldTab.isFirst()).isTrue();
    }


    @Test
    @DisplayName("존재하지 않는 플랜에 탭을 생성할 수 없다")
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
    @DisplayName("플랜에 소속되지 않은 사용자는 탭을 생성할 수 없다")
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
    @DisplayName("하나의 플랜에 탭은 최대 5개까지 생성이 가능하다")
    void createSuccessTabSizeNotOver() {
        // given
        Plan plan = createPlan();
        Member member = createMember();
        createMemberOfPlan(plan, member);
        Tab tab4 = createTab(plan, "탭4", null, false);
        Tab tab3 = createTab(plan, "탭3", tab4, false);
        Tab tab2 = createTab(plan, "탭2", tab3, false);
        Tab tab1 = createTab(plan, "탭1", tab2, true);

        tabRepository.saveAll(List.of(tab1, tab2, tab3, tab4));

        TabCreateRequest request = createTabCreateRequest(plan.getId(), "이름");

        // when
        Long savedId = tabService.create(member.getId(), request);

        // then
        assertThat(savedId).isNotNull();

        Tab savedTab = tabRepository.findById(savedId)
            .get();
        assertThat(savedTab.getTitle()).isEqualTo(request.getTitle());
        assertThat(savedTab.getPlan()
            .getId()).isEqualTo(request.getPlanId());
    }

    @Test
    @DisplayName("하나의 플랜에 탭의 개수를 초과하면 예외를 반환한다")
    void createFailTabLimitOver() {
        // given
        Plan plan = createPlan();
        Member member = createMember();
        createMemberOfPlan(plan, member);

        Tab tab1 = Tab.builder()
            .first(true)
            .plan(plan)
            .build();
        Tab tab2 = Tab.builder()
            .plan(plan)
            .build();
        Tab tab3 = Tab.builder()
            .plan(plan)
            .build();
        Tab tab4 = Tab.builder()
            .plan(plan)
            .build();
        Tab tab5 = Tab.builder()
            .plan(plan)
            .build();
        tabRepository.saveAll(List.of(tab1, tab2, tab3, tab4, tab5));

        TabCreateRequest request = createTabCreateRequest(plan.getId(), "이름");

        // when & then
        assertThatThrownBy(() -> tabService.create(member.getId(), request))
            .isInstanceOf(ApiException.class)
            .hasMessageContaining(ErrorCode.TAB_SIZE_INVALID.getMessage());
    }

    @Test
    @DisplayName("동일한 플랜에서 탭 이름은 중복될 수 없다")
    void createFailDuplicatedTabName() {
        // given
        String tabName = "탭이름";
        Plan plan = createPlan();
        Member member = createMember();
        createMemberOfPlan(plan, member);
        createTab(plan, tabName, null, true);

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

        Tab tab2 = createTab(plan, "이전탭2", null, false);
        Tab tab1 = createTab(plan, "이전탭1", tab2, true);
        tabRepository.saveAll(List.of(tab1, tab2));

        TabCreateRequest request = createTabCreateRequest(plan.getId(), "탭이름");

        // when
        Long savedId = tabService.create(member.getId(), request);

        // then
        assertThat(savedId).isNotNull();

        Tab savedTab = tabRepository.findById(savedId)
            .get();
        assertThat(tab2.getNext()).isEqualTo(savedTab);
        assertThat(savedTab.getNext()).isNull();
    }

    @Test
    @DisplayName("3번 탭을 1번 탭 뒤로 위치를 이동한다")
    void changeTabOrder() {
        // given
        Plan plan = createPlan();
        Member member = createMember();
        createMemberOfPlan(plan, member);
        Tab tab3 = createTab(plan, "탭3", null, false);
        Tab tab2 = createTab(plan, "탭2", tab3, false);
        Tab tab1 = createTab(plan, "탭1", tab2, true);

        Long targetId = tab3.getId();
        Long newPrevId = tab1.getId();

        TabChangeOrderRequest request = TabChangeOrderRequest.builder()
            .planId(plan.getId())
            .targetId(targetId)
            .newPrevId(newPrevId)
            .build();

        // when
        List<Long> result = tabService.changeOrder(member.getId(), request);

        // then
        assertThat(result).hasSize(3)
            .containsExactly(tab1.getId(), tab3.getId(), tab2.getId());
        assertThat(tab1.getNext()).isEqualTo(tab3);
        assertThat(tab3.getNext()).isEqualTo(tab2);
        assertThat(tab2.getNext()).isNull();
    }

    @Test
    @DisplayName("탭 위치 변경은 존재하는 플랜에 대해서만 생성할 수 있다")
    void changeTabOrderFailNotExistPlan() {
        // given
        Long memberId = 1L;
        TabChangeOrderRequest request = TabChangeOrderRequest.builder()
            .planId(2L)
            .targetId(3L)
            .newPrevId(4L)
            .build();

        // when & then
        assertThatThrownBy(() -> tabService.changeOrder(memberId, request))
            .isInstanceOf(ApiException.class)
            .hasMessageContaining(ErrorCode.PLAN_NOT_FOUND.getMessage());
    }

    @Test
    @DisplayName("탭을 수정하는 사람은 해당 플랜에 소속되어 있어야 한다")
    void changeTabOrderFailNotFoundMember() {
        // given
        Plan plan = createPlan();

        Tab tab3 = createTab(plan, "탭3", null, false);
        Tab tab2 = createTab(plan, "탭2", tab3, false);
        Tab tab1 = createTab(plan, "탭1", tab2, true);

        Member otherMember = createMember();

        TabChangeOrderRequest request = TabChangeOrderRequest.builder()
            .planId(plan.getId())
            .targetId(3L)
            .newPrevId(4L)
            .build();

        // when & then
        assertThatThrownBy(() -> tabService.changeOrder(otherMember.getId() + 12313, request))
            .isInstanceOf(ApiException.class)
            .hasMessageContaining(ErrorCode.MEMBER_NOT_FOUND_IN_PLAN.getMessage());
    }

    @Test
    @DisplayName("탭과 플랜이 매핑되지 않으면 예외를 반환한다")
    void changeTabOrderFailInvalidPlan() {
        // given
        Plan plan = createPlan();

        Tab tab3 = createTab(plan, "탭3", null, false);
        Tab tab2 = createTab(plan, "탭2", tab3, false);
        Tab tab1 = createTab(plan, "탭1", tab2, true);

        Plan otherPlan = createPlan();
        Member member = createMember();
        createMemberOfPlan(otherPlan, member);
        createTab(otherPlan, "탭1", null, true);

        TabChangeOrderRequest request = TabChangeOrderRequest.builder()
            .planId(otherPlan.getId())
            .targetId(tab1.getId())
            .newPrevId(tab3.getId())
            .build();

        // when & then
        assertThatThrownBy(() -> tabService.changeOrder(member.getId(), request))
            .isInstanceOf(ApiException.class)
            .hasMessageContaining(ErrorCode.TAB_NOT_FOUND_IN_PLAN.getMessage());
    }

    @Test
    @DisplayName("탭의 이름을 변경한다")
    void changeTitle() {
        // given
        Plan plan = createPlan();
        Member member = createMember();
        createMemberOfPlan(plan, member);
        Tab tab = createTab(plan, "TODO", null, true);
        String title = "변경할 제목";

        TabChangeTitleServiceRequest request = TabChangeTitleServiceRequest.builder()
            .planId(plan.getId())
            .title(title)
            .memberId(member.getId())
            .tabId(tab.getId())
            .build();

        // when
        TabChangeTitleResponse response = tabService.changeName(request);

        // then
        assertThat(response.getId()).isEqualTo(tab.getId());
        assertThat(response.getTitle()).isEqualTo(title);
    }

    @Test
    @DisplayName("탭 이름 변경 시 Plan은 존재해야 한다")
    void changeNameFailNotExistsPlan() {
        // given
        Plan plan = createPlan();
        Member member = createMember();
        createMemberOfPlan(plan, member);
        Tab tab = createTab(plan, "이름", null, true);

        TabChangeTitleServiceRequest request = TabChangeTitleServiceRequest.builder()
            .planId(123123L)
            .title("변경할 제목")
            .memberId(member.getId())
            .tabId(tab.getId())
            .build();

        // when & then
        assertThatThrownBy(
            () -> tabService.changeName(request))
            .isInstanceOf(ApiException.class)
            .hasMessageContaining(ErrorCode.PLAN_NOT_FOUND.getMessage());
    }

    @Test
    @DisplayName("해당되는 Plan에 소속된 멤버만 탭 이름을 변경할 수 있다")
    void chageNameFailNotFoundInPlan() {
        // given
        Plan plan = createPlan();
        Member member = createMember();
        Tab tab = createTab(plan, "이름", null, true);

        TabChangeTitleServiceRequest request = TabChangeTitleServiceRequest.builder()
            .planId(plan.getId())
            .title("변경할 제목")
            .memberId(member.getId())
            .tabId(tab.getId())
            .build();

        // when & then
        assertThatThrownBy(
            () -> tabService.changeName(request))
            .isInstanceOf(ApiException.class)
            .hasMessageContaining(ErrorCode.MEMBER_NOT_FOUND_IN_PLAN.getMessage());
    }

    @Test
    @DisplayName("해당 플랜에 속한 탭에 대해서만 이름을 변경할 수 있다")
    void chageNameFailNotFoundTab() {
        // given
        Plan plan = createPlan();
        Member member = createMember();
        createMemberOfPlan(plan, member);
        createTab(plan, "이름", null, true);

        Tab otherTab = createTab(null, "다른플랜의탭", null, true);

        TabChangeTitleServiceRequest request = TabChangeTitleServiceRequest.builder()
            .planId(plan.getId())
            .title("변경할 제목")
            .tabId(otherTab.getId())
            .memberId(member.getId())
            .build();

        // when & then
        assertThatThrownBy(() -> tabService.changeName(request))
            .isInstanceOf(ApiException.class)
            .hasMessageContaining(ErrorCode.TAB_NOT_FOUND_IN_PLAN.getMessage());
    }

    @Test
    @DisplayName("동일한 플랜에 속해있는 탭들은 이름이 달라야 한다")
    void changeNameFailSameName() {
        // given
        String duplicatedTitle = "동일 타이틀";

        Plan plan = createPlan();
        Member member = createMember();
        createMemberOfPlan(plan, member);
        Tab tab = createTab(plan, duplicatedTitle, null, false);
        Tab target = createTab(plan, "시작탭", tab, true);

        TabChangeTitleServiceRequest request = TabChangeTitleServiceRequest.builder()
            .planId(plan.getId())
            .title(duplicatedTitle)
            .tabId(target.getId())
            .memberId(member.getId())
            .build();

        // when & then
        assertThatThrownBy(() -> tabService.changeName(request))
            .isInstanceOf(ApiException.class)
            .hasMessageContaining(ErrorCode.TAB_NAME_DUPLICATE.getMessage());
    }

    @Test
    @DisplayName("플랜 소유자는 탭을 삭제할 수 있다")
    void deleteTab() {
        // given
        Member member = createMember();
        Plan plan = createPlanWithOwner(member);
        Tab target = createTab(plan, "두번째", null, false);
        createTab(plan, "TODO", target, true);
        TabDeleteServiceRequest request = makeDeleteRequest(member, target, plan);

        // when
        Long deletedId = tabService.delete(request);

        // then
        Tab result = tabRepository.findById(deletedId)
            .get();
        assertThat(result.isDeleted()).isTrue();
    }

    @Test
    @DisplayName("탭 삭제 시 자신의 prev 탭과 next 탭을 연결한다")
    void checkConnectingIfTabDelete() {
        // given
        Member member = createMember();
        Plan plan = createPlanWithOwner(member);
        Tab third = createTab(plan, "세번째", null, false);
        Tab second = createTab(plan, "두번째", third, false);
        Tab first = createTab(plan, "TODO", second, true);

        TabDeleteServiceRequest request = makeDeleteRequest(member, second, plan);

        // when
        tabService.delete(request);

        // then
        assertThat(first.getNext()).isEqualTo(third);
    }

    @Test
    @DisplayName("플랜 소유자가 아니면 탭을 삭제할 수 없다")
    void deleteTabFailNotOwner() {
        // given
        Member member = createMember();
        Plan plan = createPlanWithOwner(member);
        Tab target = createTab(plan, "두번째", null, false);
        createTab(plan, "TODO", target, true);

        Member otherMember = createMember();

        TabDeleteServiceRequest request = makeDeleteRequest(otherMember, target, plan);


        // when & then
        assertThatThrownBy(() -> tabService.delete(request))
            .isInstanceOf(ApiException.class)
            .hasMessageContaining(ErrorCode.AUTHORIZATION_FAIL.getMessage());
    }

    @Test
    @DisplayName("탭이 삭제되면 해당 탭에 속해있던 모든 태스크가 함께 삭제된다")
    void deleteAllRelatedTaskOnDeleteTab() {
        // given
        Member member = createMember();
        Plan plan = createPlanWithOwner(member);
        Tab target = createTab(plan, "두번째", null, false);
        createTab(plan, "TODO", target, true);

        Task task = createTask(target);

        TabDeleteServiceRequest request = makeDeleteRequest(member, target, plan);


        // when
        tabService.delete(request);

        // then
        List<Task> resultOpt = taskRepository.findAllByTabId(task.getId());
        assertThat(resultOpt).isEmpty();
    }

    @Test
    @DisplayName("탭을 조회한다")
    void testFindTab() throws Exception {
        // given
        Plan plan = createPlan();
        Tab tab2 = createTab(plan, "탭2", null, false);
        Tab tab1 = createTab(plan, "탭1", tab2, true);

        Member member = createMember();
        createMemberOfPlan(plan, member);

        // when
        TabFindResponse response = tabService.find(tab1.getId(), member.getId());

        // then
        assertThat(response.getId()).isEqualTo(tab1.getId());
        assertThat(response.getTitle()).isEqualTo(tab1.getTitle());
        assertThat(response.getNextId()).isEqualTo(tab2.getId());
    }

    @Test
    @DisplayName("특정 플랜의 마지막 탭을 조회한다")
    void testFindTabAboutLastTab() throws Exception {
        // given
        Plan plan = createPlan();
        Tab tab2 = createTab(plan, "탭2", null, false);
        Tab tab1 = createTab(plan, "탭1", tab2, true);

        Member member = createMember();
        createMemberOfPlan(plan, member);

        // when
        TabFindResponse response = tabService.find(tab2.getId(), member.getId());

        // then
        assertThat(response.getId()).isEqualTo(tab2.getId());
        assertThat(response.getTitle()).isEqualTo(tab2.getTitle());
        assertThat(response.getNextId()).isNull();
    }

    @Test
    @DisplayName("플랜에 가입된 사람만 private 플랜에 속한 탭을 조회 가능하다")
    void testFindPrivateTab() throws Exception {
        // given
        Plan plan = createPrivatePlan();
        Tab tab2 = createTab(plan, "탭2", null, false);
        Tab tab1 = createTab(plan, "탭1", tab2, true);

        Member member = createMember();
        createMemberOfPlan(plan, member);

        // when
        TabFindResponse response = tabService.find(tab2.getId(), member.getId());

        // then
        assertThat(response.getId()).isEqualTo(tab2.getId());
        assertThat(response.getTitle()).isEqualTo(tab2.getTitle());
        assertThat(response.getNextId()).isNull();
    }

    @Test
    @DisplayName("플랜에 가입되지 않은 사람은 private 플랜에 속한 탭을 조회할 수 없다")
    void testFindTabFailNotAuthorized() throws Exception {
        // given
        Plan plan = createPrivatePlan();
        Tab tab2 = createTab(plan, "탭2", null, false);
        Tab tab1 = createTab(plan, "탭1", tab2, true);

        Member member = createMember();

        // when & then
        assertThatThrownBy(() -> tabService.find(tab2.getId(), member.getId()))
            .isInstanceOf(ApiException.class)
            .hasMessageContaining(ErrorCode.MEMBER_NOT_FOUND_IN_PLAN.getMessage());
    }

    @Test
    @DisplayName("존재하지 않는 탭을 조회할 수 없다")
    void testFindTabFailNotFound() throws Exception {
        // given
        Long notRegisteredTabId = 123151L;

        Member member = createMember();

        // when & then
        assertThatThrownBy(() -> tabService.find(notRegisteredTabId, member.getId()))
            .isInstanceOf(ApiException.class)
            .hasMessageContaining(ErrorCode.TAB_NOT_FOUND.getMessage());
    }

    private Tab createTab(Plan plan, String title, Tab next, boolean isFirst) {
        Tab tab = Tab.builder()
            .plan(plan)
            .title(title)
            .next(next)
            .first(isFirst)
            .build();
        em.persist(tab);
        return tab;
    }


    @NotNull
    private MemberOfPlan createMemberOfPlan(Plan plan, Member member) {
        MemberOfPlan memberOfPlan = MemberOfPlan.builder()
            .plan(plan)
            .member(member)
            .build();
        em.persist(memberOfPlan);
        return memberOfPlan;
    }

    @NotNull
    private Task createTask(Tab tab) {
        Task task = Task.builder()
            .tab(tab)
            .build();
        em.persist(task);
        return task;
    }

    @NotNull
    private Member createMember() {
        Member member = Member.builder()
            .build();
        em.persist(member);
        return member;
    }

    @NotNull
    private Plan createPlan() {
        Plan plan = Plan.builder()
            .isPublic(true)
            .build();
        em.persist(plan);
        return plan;
    }

    private Plan createPrivatePlan() {
        Plan plan = Plan.builder()
            .isPublic(false)
            .build();
        em.persist(plan);
        return plan;
    }

    @NotNull
    private Plan createPlanWithOwner(Member owner) {
        Plan plan = Plan.builder()
            .owner(owner)
            .build();
        em.persist(plan);
        return plan;
    }


    @NotNull
    private TabCreateRequest createTabCreateRequest(Long planId, String title) {
        return TabCreateRequest.builder()
            .title(title)
            .planId(planId)
            .build();
    }

    @NotNull
    private TabDeleteServiceRequest makeDeleteRequest(Member member, Tab target, Plan plan) {
        return TabDeleteServiceRequest.builder()
            .memberId(member.getId())
            .tabId(target.getId())
            .planId(plan.getId())
            .build();

    }
}
