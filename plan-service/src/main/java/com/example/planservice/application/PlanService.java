package com.example.planservice.application;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.hibernate.Hibernate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.planservice.domain.Linkable;
import com.example.planservice.domain.member.Member;
import com.example.planservice.domain.member.repository.MemberRepository;
import com.example.planservice.domain.plan.Plan;
import com.example.planservice.domain.plan.repository.PlanRepository;
import com.example.planservice.domain.tab.Tab;
import com.example.planservice.domain.tab.repository.TabRepository;
import com.example.planservice.domain.task.Task;
import com.example.planservice.exception.ApiException;
import com.example.planservice.exception.ErrorCode;
import com.example.planservice.presentation.dto.request.PlanCreateRequest;
import com.example.planservice.presentation.dto.response.LabelOfPlanResponse;
import com.example.planservice.presentation.dto.response.MemberOfPlanResponse;
import com.example.planservice.presentation.dto.response.PlanResponse;
import com.example.planservice.presentation.dto.response.TabOfPlanResponse;
import com.example.planservice.presentation.dto.response.TaskOfPlanResponse;
import lombok.RequiredArgsConstructor;


@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PlanService {

    private final EmailService emailService;
    private final PlanRepository planRepository;
    private final MemberRepository memberRepository;
    private final TabRepository tabRepository;

    @Transactional
    public Long create(PlanCreateRequest request, Long userId) {
        Member member = memberRepository.findById(userId).orElseThrow(() -> new ApiException(
            ErrorCode.MEMBER_NOT_FOUND));
        Plan plan = Plan.builder()
            .title(request.getTitle())
            .intro(request.getIntro())
            .isPublic(request.isPublic())
            .owner(member)
            .build();

        sendInviteMail(request.getInvitedEmails(), request.getTitle());
        createDefaultTab(plan);

        Plan savedPlan = planRepository.save(plan);
        return savedPlan.getId();
    }

    @Transactional
    public PlanResponse getTotalPlanResponse(Long planId) {
        Plan plan = planRepository.findById(planId).orElseThrow(() -> new ApiException(ErrorCode.PLAN_NOT_FOUND));
        List<MemberOfPlanResponse> members = plan.getMembers().parallelStream().map(member ->
            new MemberOfPlanResponse().toPlanResponse(member.getMember(), plan.getOwner().getId())
        ).toList();

        List<Tab> tabList = plan.getTabs();

        List<Long> tabOrder = orderByNext(tabList);

        List<TabOfPlanResponse> tabs = tabList.parallelStream().map(tab -> {
            List<Task> tasksOfTab = tab.getTasks();
            List<Long> taskOrder = orderByNext(tasksOfTab);
            return new TabOfPlanResponse().toPlanResponse(tab, taskOrder);
        }).toList();

        List<LabelOfPlanResponse> labels =
            plan.getLabels().stream().map(label -> new LabelOfPlanResponse().toPlanResponse(label)
            ).toList();

        List<TaskOfPlanResponse> tasks =
            plan.getTasks().stream().map(task -> new TaskOfPlanResponse().toPlanResponse(task)
            ).toList();

        return PlanResponse.builder()
            .title(plan.getTitle())
            .description(plan.getIntro())
            .members(members)
            .tabOrder(tabOrder)
            .tabs(tabs)
            .tasks(tasks)
            .labels(labels)
            .isPublic(plan.isPublic())
            .build();
    }

    private void sendInviteMail(List<String> invitedEmails, String title) {
        invitedEmails.forEach(email -> emailService.sendEmail(email, title));
    }

    private void createDefaultTab(Plan plan) {
        Tab firstTab = Tab.builder().name("ToDo").plan(plan).build();
        Tab lastTab = Tab.builder().name("Done").plan(plan).build();
        firstTab.connect(lastTab);
        tabRepository.save(firstTab);
        tabRepository.save(lastTab);
    }

    @Transactional
    public <T extends Linkable<T>> List<Long> orderByNext(List<T> items) {
        List<Long> orderedItems = new ArrayList<>();
        Hibernate.initialize(items);
        Set<T> allNodes = new HashSet<>(items);

        T start = null;
        for (T item : items) {
            if (item.getNext() != null) {
                allNodes.remove(item.getNext());
            }
        }
        if (!allNodes.isEmpty()) {
            start = allNodes.iterator().next();
        }

        T current = start;
        while (current != null) {
            orderedItems.add(current.getId());
            current = current.getNext();
        }

        return orderedItems;
    }

}
