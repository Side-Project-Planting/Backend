package com.example.planservice.application;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
import com.example.planservice.presentation.dto.request.PlanUpdateRequest;
import com.example.planservice.presentation.dto.response.LabelOfPlanResponse;
import com.example.planservice.presentation.dto.response.MemberOfPlanResponse;
import com.example.planservice.presentation.dto.response.PlanResponse;
import com.example.planservice.presentation.dto.response.PlanTitleIdResponse;
import com.example.planservice.presentation.dto.response.TabOfPlanResponse;
import com.example.planservice.presentation.dto.response.TaskOfPlanResponse;
import lombok.RequiredArgsConstructor;


@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PlanService {

    private final EmailService emailService;
    private final TabService tabService;
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
        sendInviteMail(request.getInvitedEmails(), request.getTitle(), member.getId());
        Plan savedPlan = planRepository.save(plan);

        createDefaultTab(plan);

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
        List<Long> tabOrder = getSortedTabID(tabList);
        List<TabOfPlanResponse> tabs = getTabResponses(tabList);

        return PlanResponse.builder()
            .id(plan.getId())
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

    @Transactional
    public Long exit(Long planId, Long memberId) {
        memberOfPlanRepository.deleteByPlanIdAndMemberId(planId, memberId);
        return memberId;
    }

    @Transactional
    public Long kick(Long planId, Long kickingMemberId, Long userId) {
        Plan plan = planRepository.findById(planId)
            .orElseThrow(() -> new ApiException(ErrorCode.PLAN_NOT_FOUND));
        validateOwner(plan.getOwner()
            .getId(), userId);
        MemberOfPlan memberOfPlan = memberOfPlanRepository.findByPlanIdAndMemberId(kickingMemberId, planId)
            .orElseThrow(() -> new ApiException(ErrorCode.MEMBER_NOT_FOUND));
        memberOfPlanRepository.delete(memberOfPlan);
        return memberOfPlan.getId();
    }

    @Transactional
    public Long delete(Long planId, Long userId) {
        MemberOfPlan memberOfPlan = memberOfPlanRepository.findByPlanIdAndMemberId(planId, userId)
            .orElseThrow(() -> new ApiException(ErrorCode.MEMBER_NOT_FOUND_IN_PLAN));
        validateOwner(memberOfPlan.getPlan()
            .getOwner()
            .getId(), userId);

        Plan plan = memberOfPlan.getPlan();
        plan.softDelete();
        return plan.getId();
    }

    @Transactional
    public Long update(Long planId, PlanUpdateRequest request, Long userId) {
        Plan plan = planRepository.findById(planId)
            .orElseThrow(() -> new ApiException(ErrorCode.PLAN_NOT_FOUND));
        validateOwner(plan.getOwner()
            .getId(), userId);
        Member nextOwner = memberRepository.findById(request.getOwnerId())
            .orElseThrow(() -> new ApiException(ErrorCode.MEMBER_NOT_FOUND));
        plan.update(request.getTitle(), request.getIntro(), nextOwner, request.isPublic());
        return plan.getId();
    }

    public List<PlanTitleIdResponse> getAllPlanByMemberId(Long userId) {
        List<MemberOfPlan> memberOfPlans = memberOfPlanRepository.findAllByMemberId(userId)
            .orElseThrow(() -> new ApiException(ErrorCode.MEMBER_NOT_FOUND));

        return memberOfPlans.stream()
            .map(memberOfPlan -> PlanTitleIdResponse.from(memberOfPlan.getPlan()))
            .toList();
    }

    private void validateOwner(Long ownerId, Long userId) {
        if (!ownerId
            .equals(userId)) {
            throw new ApiException(ErrorCode.AUTHORIZATION_FAIL);
        }
    }

    private void sendInviteMail(List<String> invitedEmails, String title, Long userId) {
        invitedEmails.forEach(email -> emailService.sendInviteEmail(email, title, userId));
    }

    private void createDefaultTab(Plan plan) {
        Tab todoTab = Tab.create(plan, "Todo");
        Tab inprogressTab = Tab.create(plan, "In progress");
        Tab doneTab = Tab.create(plan, "Done");

        todoTab.connect(inprogressTab);
        inprogressTab.connect(doneTab);

        plan.getTabs()
            .add(todoTab);
        plan.getTabs()
            .add(inprogressTab);
        plan.getTabs()
            .add(doneTab);

        tabRepository.saveAll(List.of(todoTab, inprogressTab, doneTab));

        tabService.createDummyTask(todoTab);
        tabService.createDummyTask(inprogressTab);
        tabService.createDummyTask(doneTab);
    }

    private List<MemberOfPlanResponse> getMemberResponses(List<MemberOfPlan> members, Long ownerId) {
        return members.stream()
            .map(member -> MemberOfPlanResponse.from(member.getMember(), ownerId))
            .toList();
    }

    private List<TabOfPlanResponse> getTabResponses(List<Tab> tabList) {
        return tabList.stream()
            .map(tab -> {
                List<Task> tasksOfTab = tab.getTasks();
                List<Long> taskOrder = getSortedTaskID(tasksOfTab);
                return TabOfPlanResponse.from(tab, taskOrder);
            })
            .toList();
    }

    private List<LabelOfPlanResponse> getLabelResponses(List<Label> labels) {
        return labels.stream()
            .map(LabelOfPlanResponse::from)
            .toList();
    }

    private List<TaskOfPlanResponse> getTaskResponses(List<Task> tasks) {
        return tasks.stream()
            .map(TaskOfPlanResponse::from)
            .toList();
    }

    public List<Long> getSortedTaskID(List<Task> items) {
        List<Long> orderedItems = new ArrayList<>();
        Set<Task> allNodes = new HashSet<>(items);
        Task start = null;
        for (Task item : items) {
            if (item.getNext() != null) {
                allNodes.remove(item.getNext());
            }
        }
        if (!allNodes.isEmpty()) {
            start = allNodes.iterator()
                .next();
        }

        Task current = start;
        while (current != null) {
            orderedItems.add(current.getId());
            current = current.getNext();
        }

        return orderedItems;
    }

    public List<Long> getSortedTabID(List<Tab> items) {
        List<Long> orderedItems = new ArrayList<>();
        Set<Tab> allNodes = new HashSet<>(items);
        Tab start = null;
        for (Tab item : items) {
            if (item.getNext() != null) {
                allNodes.remove(item.getNext());
            }
        }
        if (!allNodes.isEmpty()) {
            start = allNodes.iterator()
                .next();
        }

        Tab current = start;
        while (current != null) {
            orderedItems.add(current.getId());
            current = current.getNext();
        }

        return orderedItems;
    }


}
