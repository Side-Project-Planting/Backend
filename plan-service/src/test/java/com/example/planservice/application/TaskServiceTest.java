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
    @DisplayName("탭의 첫 번째 태스크를 생성한다")
    void createTaskOrderFirst() {
        // given
        Plan plan = createPlan();
        Tab tab = createTab(plan);
        Member member = createMemberWithPlan(plan);
        Member taskManager = createMemberWithPlan(plan);

        TaskCreateRequest request = TaskCreateRequest.builder()
            .tabId(tab.getId())
            .managerId(taskManager.getId())
            .name("스프링공부하기")
            .description("1. 책을 편다. \n2. 글자를 읽는다. \n3. 책을닫는다\n")
            .startDate(null)
            .endDate(null)
            .build();

        // when
        Long createdId = taskService.create(member.getId(), request);

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
    @DisplayName("탭의 N번째(첫 번째가 아닌) 태스크를 생성한다")
    void createTaskOrderNotFirst() {
        // given
        Task originalFirstTab = createTask();
        Plan plan = createPlan();
        Tab tab = createTabWithLastTask(originalFirstTab, plan);
        Member loginMember = createMemberWithPlan(plan);
        Member manager = createMemberWithPlan(plan);

        TaskCreateRequest request = TaskCreateRequest.builder()
            .tabId(tab.getId())
            .managerId(manager.getId())
            .name("스프링공부하기")
            .description("1. 책을 편다. \n2. 글자를 읽는다. \n3. 책을닫는다\n")
            .startDate(null)
            .endDate(null)
            .build();

        // when
        Long createdId = taskService.create(loginMember.getId(), request);

        // then
        Task task = taskRepository.findById(createdId).get();

        assertThat(task.getPrev()).isEqualTo(originalFirstTab);
        assertThat(task.getNext()).isNull();
        assertThat(originalFirstTab.getNext()).isEqualTo(task);

        assertThat(task.getId()).isEqualTo(createdId);
        assertThat(task.getTab().getId()).isEqualTo(request.getTabId());
        assertThat(task.getManager().getId()).isEqualTo(request.getManagerId());
        assertThat(task.getName()).isEqualTo(request.getName());
        assertThat(task.getDescription()).isEqualTo(request.getDescription());
        assertThat(task.getStartDate()).isEqualTo(request.getStartDate());
        assertThat(task.getEndDate()).isEqualTo(request.getEndDate());

    }

    @Test
    @DisplayName("태스크는 플랜에 소속된 사람만 만들 수 있다")
    void createFailNotExistMemberInPlan() {
        // given
        Member member = createMember();
        Member manager = createMember();
        Plan plan = createPlan();
        Tab tab = createTab(plan);

        TaskCreateRequest request = TaskCreateRequest.builder()
            .tabId(tab.getId())
            .managerId(manager.getId())
            .name("스프링공부하기")
            .description("1. 책을 편다. \n2. 글자를 읽는다. \n3. 책을닫는다\n")
            .startDate(null)
            .endDate(null)
            .build();

        // when & then
        assertThatThrownBy(() -> taskService.create(member.getId(), request))
            .isInstanceOf(ApiException.class)
            .hasMessageContaining(ErrorCode.MEMBER_NOT_FOUND_IN_PLAN.getMessage());
    }


    private Plan createPlan() {
        Plan plan = Plan.builder().build();
        planRepository.save(plan);
        return plan;
    }

    private Member createMemberWithPlan(Plan plan) {
        Member member = createMember();
        createMemberOfPlan(plan, member);
        return member;
    }

    private Tab createTab(Plan plan) {
        Tab.TabBuilder builder = Tab.builder();
        if (plan != null) {
            builder.plan(plan);
        }
        Tab tab = builder.build();
        tabRepository.save(tab);
        return tab;
    }

    private Tab createTabWithLastTask(@NotNull Task lastTask, Plan plan) {
        Tab.TabBuilder builder = Tab.builder();
        if (plan != null) {
            builder.plan(plan);
        }
        Tab tab = builder.lastTask(lastTask).build();
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