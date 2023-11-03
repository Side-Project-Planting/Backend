package com.example.planservice.application;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.planservice.domain.Linkable;
import com.example.planservice.domain.label.Label;
import com.example.planservice.domain.member.Member;
import com.example.planservice.domain.member.repository.MemberRepository;
import com.example.planservice.domain.memberofplan.MemberOfPlan;
import com.example.planservice.domain.memberofplan.repository.MemberOfPlanRepository;
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
    private final MemberOfPlanRepository memberOfPlanRepository;

    @Transactional
    public Long create(PlanCreateRequest request, Long userId) {
        Member member = memberRepository.findById(userId)
            .orElseThrow(() -> new ApiException(
                ErrorCode.MEMBER_NOT_FOUND));
        Plan plan = Plan.builder()
            .title(request.getTitle())
            .intro(request.getIntro())
            .isPublic(request.isPublic())
            .owner(member)
            .build();

        MemberOfPlan memberOfPlan = MemberOfPlan.builder()
            .member(member)
            .plan(plan)
            .build();
        memberOfPlanRepository.save(memberOfPlan);

        sendInviteMail(request.getInvitedEmails(), request.getTitle());
        createDefaultTab(plan);

        Plan savedPlan = planRepository.save(plan);
        return savedPlan.getId();
    }

    public PlanResponse getTotalPlanResponse(Long planId) {
        Plan plan = planRepository.findById(planId)
            .orElseThrow(() -> new ApiException(ErrorCode.PLAN_NOT_FOUND));
        List<Tab> tabList = tabRepository.findAllByPlanId(planId);

        List<MemberOfPlanResponse> members = getMemberResponses(plan.getMembers(), plan.getOwner()
            .getId());
        List<LabelOfPlanResponse> labels = getLabelResponses(plan.getLabels());
        List<TaskOfPlanResponse> tasks = getTaskResponses(plan.getTasks());
        List<Long> tabOrder = orderByNext(tabList);
        List<TabOfPlanResponse> tabs = getTabResponses(tabList);

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

    @Transactional
    public Long inviteMember(Long planId, Long memberId) {
        Plan plan = planRepository.findById(planId)
            .orElseThrow(() -> new ApiException(ErrorCode.PLAN_NOT_FOUND));
        Member member =
            memberRepository.findById(memberId)
                .orElseThrow(() -> new ApiException(ErrorCode.MEMBER_NOT_FOUND));
        MemberOfPlan memberOfPlan = MemberOfPlan.builder()
            .member(member)
            .plan(plan)
            .build();
        memberOfPlanRepository.save(memberOfPlan);
        return memberOfPlan.getId();
    }

    private void sendInviteMail(List<String> invitedEmails, String title) {
        invitedEmails.forEach(email -> emailService.sendEmail(email, title));
    }

    private void createDefaultTab(Plan plan) {
        Tab firstTab = Tab.builder()
            .name("ToDo")
            .plan(plan)
            .build();
        Tab lastTab = Tab.builder()
            .name("Done")
            .plan(plan)
            .build();
        firstTab.connect(lastTab);
        tabRepository.save(firstTab);
        tabRepository.save(lastTab);
    }

    private List<MemberOfPlanResponse> getMemberResponses(List<MemberOfPlan> members, Long ownerId) {
        return members.parallelStream()
            .map(member ->
                new MemberOfPlanResponse().toPlanResponse(member.getMember(), ownerId)
            )
            .toList();
    }

    private List<TabOfPlanResponse> getTabResponses(List<Tab> tabList) {
        return tabList.parallelStream()
            .map(tab -> {
                List<Task> tasksOfTab = tab.getTasks();
                List<Long> taskOrder = orderByNext(tasksOfTab);
                return new TabOfPlanResponse().toPlanResponse(tab, taskOrder);
            })
            .toList();
    }

    private List<LabelOfPlanResponse> getLabelResponses(List<Label> labels) {
        return labels.stream()
            .map(label -> new LabelOfPlanResponse().toPlanResponse(label)
            )
            .toList();
    }

    private List<TaskOfPlanResponse> getTaskResponses(List<Task> tasks) {
        return tasks.stream()
            .map(task -> new TaskOfPlanResponse().toPlanResponse(task)
            )
            .toList();
    }

    public <T extends Linkable<T>> List<Long> orderByNext(List<T> items) {
        List<Long> orderedItems = new ArrayList<>();
        Set<T> allNodes = new HashSet<>(items);
        T start = null;
        for (T item : items) {
            if (item.getNext() != null) {
                allNodes.remove(item.getNext());
            }
        }
        if (!allNodes.isEmpty()) {
            start = allNodes.iterator()
                .next();
        }

        T current = start;
        while (current != null) {
            orderedItems.add(current.getId());
            current = current.getNext();
        }

        return orderedItems;
    }
}
