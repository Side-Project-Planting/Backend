package com.example.planservice.application;

import static java.util.Objects.requireNonNull;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Stream;

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
import com.example.planservice.domain.tab.TabGroup;
import com.example.planservice.domain.tab.repository.TabRepository;
import com.example.planservice.domain.task.Task;
import com.example.planservice.domain.task.repository.TaskRepository;
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
import com.example.planservice.util.RedisUtils;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PlanService {

    private final EmailService emailService;
    private final RedisUtils redisUtils;
    private final TaskRepository taskRepository;
    private final PlanRepository planRepository;
    private final MemberRepository memberRepository;
    private final TabRepository tabRepository;
    private final MemberOfPlanRepository memberOfPlanRepository;


    @Transactional
    public Long create(PlanCreateRequest request, Long userId) {
        Member member = memberRepository.findById(userId)
            .orElseThrow(() -> new ApiException(ErrorCode.MEMBER_NOT_FOUND));
        Plan plan = request.toEntity(member);
        createDefaultTab(plan);
        MemberOfPlan memberOfPlan = MemberOfPlan.create(member, plan);
        memberOfPlanRepository.save(memberOfPlan);
        sendInviteMail(request.getInvitedEmails(), request.getTitle(), member.getId());
        Plan savedPlan = planRepository.save(plan);
        return savedPlan.getId();
    }

    public PlanResponse getTotalPlanResponse(Long planId) {
        Plan plan = planRepository.findById(planId)
            .orElseThrow(() -> new ApiException(ErrorCode.PLAN_NOT_FOUND));

        TabGroup tabGroup = new TabGroup(plan.getId(), plan.getTabs());
        List<Tab> sortedTabs = tabGroup.getSortedTabs();
        List<MemberOfPlanResponse> members = getMemberResponses(plan.getMembers(), plan.getOwner()
            .getId());

        List<Task> allTask = sortedTabs.stream()
            .map(Tab::getSortedTasks)
            .flatMap(List::stream)
            .toList();

        List<LabelOfPlanResponse> labels = getLabelResponses(plan.getLabels());
        List<TaskOfPlanResponse> tasks = getTaskResponses(allTask);
        List<Long> tabOrder = sortedTabs.stream()
            .map(Tab::getId)
            .toList();

        List<TabOfPlanResponse> tabs = getTabResponses(sortedTabs);

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
    public Long inviteMember(String uuid, Long memberId) {
        Long planId = checkInvitedUUID(uuid);

        if (planId == null) {
            throw new ApiException(ErrorCode.MEMBER_NOT_FOUND);
        }

        if (isDuplicatedMember(planId, memberId)) {
            throw new ApiException(ErrorCode.MEMBER_ALREADY_REGISTERED);
        }


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

    public boolean isDuplicatedMember(Long planId, Long memberId) {
        return memberOfPlanRepository.existsByPlanIdAndMemberId(planId, memberId);
    }

    @Transactional
    public void exit(Long planId, Long memberId) {
        memberOfPlanRepository.deleteByPlanIdAndMemberId(planId, memberId);
    }


    @Transactional
    public void delete(Long planId, Long userId) {
        MemberOfPlan memberOfPlan = memberOfPlanRepository.findByPlanIdAndMemberId(planId, userId)
            .orElseThrow(() -> new ApiException(ErrorCode.MEMBER_NOT_FOUND_IN_PLAN));
        validateOwner(memberOfPlan.getPlan()
            .getOwner()
            .getId(), userId);

        Plan plan = memberOfPlan.getPlan();
        plan.softDelete();
        planRepository.save(plan);
        memberOfPlanRepository.delete(memberOfPlan);
    }

    @Transactional
    public void update(Long planId, PlanUpdateRequest request, Long userId) {
        Plan plan = planRepository.findById(planId)
            .orElseThrow(() -> new ApiException(ErrorCode.PLAN_NOT_FOUND));
        validateOwner(plan.getOwner()
            .getId(), userId);

        if (!request.getInvitedEmails().isEmpty()) {
            sendInviteMail(request.getInvitedEmails(), request.getTitle(), userId);
        }

        if (!request.getKickingMemberIds().isEmpty()) {
            kick(planId, request.getKickingMemberIds());
        }


        Member nextOwner = memberRepository.findById(request.getOwnerId())
            .orElseThrow(() -> new ApiException(ErrorCode.MEMBER_NOT_FOUND));
        plan.update(request.getTitle(), request.getIntro(), nextOwner, request.isPublic());

        planRepository.save(plan);

    }

    public void kick(Long planId, List<Long> kinkingMemberIds) {
        memberOfPlanRepository.deleteAllByPlanIdAndMemberIds(planId, kinkingMemberIds);
    }

    public List<PlanTitleIdResponse> getAllPlanTitleIdByMemberId(Long userId) {
        List<MemberOfPlan> memberOfPlans = memberOfPlanRepository.findAllByMemberId(userId)
            .orElseThrow(() -> new ApiException(ErrorCode.MEMBER_NOT_FOUND));

        return memberOfPlans.stream()
            .map(memberOfPlan -> PlanTitleIdResponse.from(memberOfPlan.getPlan()))
            .toList();
    }

    private void validateOwner(Long ownerId, Long userId) {
        if (!ownerId.equals(userId)) {
            throw new ApiException(ErrorCode.AUTHORIZATION_FAIL);
        }
    }

    private void sendInviteMail(List<String> invitedEmails, String title, Long planId) {
        requireNonNull(invitedEmails, "초대할 이메일을 입력해주세요");
        requireNonNull(title, "플랜 제목을 입력해주세요");
        requireNonNull(planId, "플랜 아이디를 입력해주세요");

        String uuid = UUID.randomUUID().toString();
        savePlanIdToRedis(uuid, planId);
        invitedEmails.forEach(email -> emailService.sendInviteEmail(email, title, uuid));
    }

    private void createDefaultTab(Plan plan) {
        Tab firstTab = Tab.createTodoTab(plan);
        Tab secondTab = Tab.create(plan, Tab.IN_PROGRESS);
        Tab lastTab = Tab.create(plan, Tab.DONE);
        firstTab.connect(secondTab);
        secondTab.connect(lastTab);
        tabRepository.saveAll(List.of(firstTab, secondTab, lastTab));

        List<Task> allDummyTasks = Stream.of(
                Task.createFirstAndLastDummy(firstTab),
                Task.createFirstAndLastDummy(secondTab),
                Task.createFirstAndLastDummy(lastTab))
            .flatMap(List::stream)
            .toList();
        taskRepository.saveAll(allDummyTasks);
    }

    private List<MemberOfPlanResponse> getMemberResponses(List<MemberOfPlan> members, Long ownerId) {
        return members.stream()
            .map(member -> MemberOfPlanResponse.from(member.getMember(), ownerId))
            .toList();
    }

    private List<TabOfPlanResponse> getTabResponses(List<Tab> tabList) {
        return tabList.stream()
            .filter(tab -> !tab.isDeleted())
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
            .filter(task -> !task.isDeleted())
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
            if (current.isDummy()) {
                current = current.getNext();
                continue;
            }
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

    public boolean isDeletedPlan(Long planId) {
        Plan plan = planRepository.findById(planId)
            .orElseThrow(() -> new ApiException(ErrorCode.PLAN_NOT_FOUND));
        return plan.isDeleted();
    }

    private Long checkInvitedUUID(String uuid) {
        return Long.parseLong(redisUtils.getData(uuid));
    }

    private void savePlanIdToRedis(String uuid, Long planId) {
        redisUtils.setData(uuid, planId.toString(), 1000L * 60 * 60 * 24);
    }
}
