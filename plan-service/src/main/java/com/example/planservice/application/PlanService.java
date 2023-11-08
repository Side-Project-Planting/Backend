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
import com.example.planservice.presentation.dto.request.PlanUpdateRequest;
import com.example.planservice.presentation.dto.request.TabCreateRequest;
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

    @Transactional
    public Long exit(Long planId, Long memberId) {
        MemberOfPlan memberOfPlan = memberOfPlanRepository.findByPlanIdAndMemberId(memberId, planId)
            .orElseThrow(() -> new ApiException(ErrorCode.MEMBER_NOT_FOUND));
        memberOfPlanRepository.delete(memberOfPlan);
        return memberOfPlan.getId();
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
        Plan plan = planRepository.findById(planId)
            .orElseThrow(() -> new ApiException(ErrorCode.PLAN_NOT_FOUND));
        validateOwner(plan.getOwner()
            .getId(), userId);
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

    private void validateOwner(Long ownerId, Long userId) {
        if (!ownerId
            .equals(userId)) {
            throw new ApiException(ErrorCode.AUTHORIZATION_FAIL);
        }
    }

    private void sendInviteMail(List<String> invitedEmails, String title) {
        invitedEmails.forEach(email -> emailService.sendEmail(email, title));
    }

    private void createDefaultTab(Plan plan) {
        TabCreateRequest todoCreateRequest = TabCreateRequest.builder()
            .name("To Do")
            .planId(plan.getId())
            .build();
        TabCreateRequest inprogressCreateRequest = TabCreateRequest.builder()
            .name("In Progress")
            .planId(plan.getId())
            .build();
        TabCreateRequest doneCreateRequest = TabCreateRequest.builder()
            .name("Done")
            .planId(plan.getId())
            .build();
        tabService.create(plan.getId(), todoCreateRequest);
        tabService.create(plan.getId(), inprogressCreateRequest);
        tabService.create(plan.getId(), doneCreateRequest);
    }

    private List<MemberOfPlanResponse> getMemberResponses(List<MemberOfPlan> members, Long ownerId) {
        return members.stream()
            .map(member -> MemberOfPlanResponse.to(member.getMember(), ownerId))
            .toList();
    }

    private List<TabOfPlanResponse> getTabResponses(List<Tab> tabList) {
        return tabList.stream()
            .map(tab -> {
                List<Task> tasksOfTab = tab.getTasks();
                List<Long> taskOrder = orderByNext(tasksOfTab);
                return TabOfPlanResponse.to(tab, taskOrder);
            })
            .toList();
    }

    private List<LabelOfPlanResponse> getLabelResponses(List<Label> labels) {
        return labels.stream()
            .map(LabelOfPlanResponse::to)
            .toList();
    }

    private List<TaskOfPlanResponse> getTaskResponses(List<Task> tasks) {
        return tasks.stream()
            .map(TaskOfPlanResponse::to)
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
