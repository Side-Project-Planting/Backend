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
import com.example.planservice.domain.tab.Tab;
import com.example.planservice.domain.tab.repository.TabRepository;
import com.example.planservice.domain.task.Task;
import com.example.planservice.domain.task.repository.TaskRepository;
import com.example.planservice.exception.ApiException;
import com.example.planservice.exception.ErrorCode;
import com.example.planservice.presentation.dto.request.TaskCreateRequest;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
@SuppressWarnings("squid:S5778")
class TaskServiceTest {
    @Autowired
    TaskService taskService;

    @Autowired
    TaskRepository taskRepository;

    @Autowired
    TabRepository tabRepository;

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    PlanRepository planRepository;

    @Autowired
    MemberOfPlanRepository memberOfPlanRepository;

    @Test
    @DisplayName("특정 탭의 첫 태스크를 생성한다")
    void createTaskOrderFirst() {
        // given
        Member loginMember = createMember();
        Member manager = createMember();
        Tab tab = createTab();
        Plan plan = createPlan();
        createMemberOfPlan(plan, loginMember);

        TaskCreateRequest request = TaskCreateRequest.builder()
            .planId(plan.getId())
            .tabId(tab.getId())
            .managerId(manager.getId())
            .name("스프링공부하기")
            .description("1. 책을 편다. \n2. 글자를 읽는다. \n3. 책을닫는다\n")
            .startDate(null)
            .endDate(null)
            .build();

        // when
        Long createdId = taskService.createTask(loginMember.getId(), request);

        // then
        Task task = taskRepository.findById(createdId).get();

        assertThat(task.getId()).isEqualTo(createdId);
        assertThat(task.getTab().getId()).isEqualTo(request.getTabId());
        assertThat(task.getManager().getId()).isEqualTo(request.getManagerId());
        assertThat(task.getName()).isEqualTo(request.getName());
        assertThat(task.getDescription()).isEqualTo(request.getDescription());
        assertThat(task.getStartDate()).isEqualTo(request.getStartDate());
        assertThat(task.getEndDate()).isEqualTo(request.getEndDate());

        assertThat(tab.getLastTask()).isEqualTo(task);
    }

    @Test
    @DisplayName("특정 탭의 N번째 태스크를 생성한다(첫 번째가 아님)")
    void createTaskOrderNotFirst() {
        // given
        Member loginMember = createMember();
        Member manager = createMember();
        Task originalFirstTab = createTask();
        Tab tab = createTabWithLastTask(originalFirstTab);
        Plan plan = createPlan();
        createMemberOfPlan(plan, loginMember);

        TaskCreateRequest request = TaskCreateRequest.builder()
            .planId(plan.getId())
            .tabId(tab.getId())
            .managerId(manager.getId())
            .name("스프링공부하기")
            .description("1. 책을 편다. \n2. 글자를 읽는다. \n3. 책을닫는다\n")
            .startDate(null)
            .endDate(null)
            .build();

        // when
        Long createdId = taskService.createTask(loginMember.getId(), request);

        // then
        Task task = taskRepository.findById(createdId).get();

        assertThat(task.getId()).isEqualTo(createdId);
        assertThat(task.getTab().getId()).isEqualTo(request.getTabId());
        assertThat(task.getManager().getId()).isEqualTo(request.getManagerId());
        assertThat(task.getName()).isEqualTo(request.getName());
        assertThat(task.getDescription()).isEqualTo(request.getDescription());
        assertThat(task.getStartDate()).isEqualTo(request.getStartDate());
        assertThat(task.getEndDate()).isEqualTo(request.getEndDate());

        assertThat(task.getPrev()).isEqualTo(originalFirstTab);
        assertThat(originalFirstTab.getNext()).isEqualTo(task);
    }

    @Test
    @DisplayName("존재하지 않는 플랜에는 태스크를 만들 수 없다")
    void createFailNotExistPlan() {
        // given
        Member loginMember = createMember();
        Member manager = createMember();
        Tab tab = createTab();

        TaskCreateRequest request = TaskCreateRequest.builder()
            .planId(123123L)
            .tabId(tab.getId())
            .managerId(manager.getId())
            .name("스프링공부하기")
            .description("1. 책을 편다. \n2. 글자를 읽는다. \n3. 책을닫는다\n")
            .startDate(null)
            .endDate(null)
            .build();

        // when & then
        assertThatThrownBy(() -> taskService.createTask(loginMember.getId(), request))
            .isInstanceOf(ApiException.class)
            .hasMessageContaining(ErrorCode.PLAN_NOT_FOUND.getMessage());
    }

    @Test
    @DisplayName("플랜에 속한 사람만 태스크를 만들 수 있다")
    void createFailNotExistMemberInPlan() {
        // given
        Member loginMember = createMember();
        Member manager = createMember();
        Tab tab = createTab();
        Plan plan = createPlan();

        TaskCreateRequest request = TaskCreateRequest.builder()
            .planId(plan.getId())
            .tabId(tab.getId())
            .managerId(manager.getId())
            .name("스프링공부하기")
            .description("1. 책을 편다. \n2. 글자를 읽는다. \n3. 책을닫는다\n")
            .startDate(null)
            .endDate(null)
            .build();

        // when & then
        assertThatThrownBy(() -> taskService.createTask(loginMember.getId(), request))
            .isInstanceOf(ApiException.class)
            .hasMessageContaining(ErrorCode.MEMBER_NOT_FOUND_IN_PLAN.getMessage());
    }


    private Plan createPlan() {
        Plan plan = Plan.builder().build();
        planRepository.save(plan);
        return plan;
    }

    private Tab createTab() {
        Tab tab = Tab.builder().build();
        tabRepository.save(tab);
        return tab;
    }

    private Tab createTabWithLastTask(Task lastTask) {
        Tab tab = Tab.builder()
            .lastTask(lastTask)
            .build();
        tabRepository.save(tab);
        return tab;
    }

    private Member createMember() {
        Member member = Member.builder().build();
        memberRepository.save(member);
        return member;
    }

    private Task createTask() {
        Task task = Task.builder().build();
        taskRepository.save(task);
        return task;
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

}