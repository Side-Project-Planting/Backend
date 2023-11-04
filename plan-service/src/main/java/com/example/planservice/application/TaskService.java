package com.example.planservice.application;

import java.util.List;
import java.util.Objects;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.planservice.domain.label.repository.LabelRepository;
import com.example.planservice.domain.member.Member;
import com.example.planservice.domain.plan.Plan;
import com.example.planservice.domain.tab.Tab;
import com.example.planservice.domain.tab.repository.TabRepository;
import com.example.planservice.domain.task.LabelOfTask;
import com.example.planservice.domain.task.Task;
import com.example.planservice.domain.task.repository.LabelOfTaskRepository;
import com.example.planservice.domain.task.repository.TaskRepository;
import com.example.planservice.exception.ApiException;
import com.example.planservice.exception.ErrorCode;
import com.example.planservice.presentation.dto.request.TaskChangeOrderRequest;
import com.example.planservice.presentation.dto.request.TaskCreateRequest;
import com.example.planservice.presentation.dto.request.TaskUpdateRequest;
import lombok.RequiredArgsConstructor;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class TaskService {
    private final TaskRepository taskRepository;
    private final TabRepository tabRepository;
    private final PlanMembershipService planMembershipService;
    private final LabelOfTaskRepository labelOfTaskRepository;
    private final LabelRepository labelRepository;

    @Transactional
    public Long create(Long memberId, TaskCreateRequest request) {
        Tab tab = tabRepository.findById(request.getTabId())
            .orElseThrow(() -> new ApiException(ErrorCode.TAB_NOT_FOUND_IN_PLAN));
        planMembershipService.validateMemberIsInThePlan(memberId, tab.getPlan());
        Member manager = planMembershipService.getMemberBelongingToPlan(request.getManagerId(), tab.getPlan());

        Task task = Task.builder()
            .tab(tab)
            .manager(manager)
            .name(request.getName())
            .description(request.getDescription())
            .build();

        Task savedTask = taskRepository.save(task);
        saveAllLabelOfTask(request.getLabels(), task, tab.getPlan());
        addEndOfTab(savedTask, tab);
        return savedTask.getId();
    }

    @Transactional
    public Long updateContents(Long memberId, TaskUpdateRequest request) {
        Plan plan = planMembershipService.getPlanAfterValidateAuthorization(request.getPlanId(), memberId);
        Task task = taskRepository.findById(request.getTaskId())
            .orElseThrow(() -> new ApiException(ErrorCode.TASK_NOT_FOUND));
        Member manager = planMembershipService.getMemberBelongingToPlan(request.getManagerId(), plan);

        task.change(request.toEntity(manager));
        List<LabelOfTask> labelOfTaskList = labelOfTaskRepository.findAllByTaskId(task.getId());
        labelOfTaskRepository.deleteAllInBatch(labelOfTaskList);
        saveAllLabelOfTask(request.getLabels(), task, plan);

        return task.getId();
    }

    @Transactional
    public List<Long> changeOrder(Long memberId, TaskChangeOrderRequest request) {
        Tab tab = tabRepository.findById(request.getTargetTabId())
            .orElseThrow(() -> new ApiException(ErrorCode.TAB_NOT_FOUND_IN_PLAN));
        planMembershipService.validateMemberIsInThePlan(memberId, tab.getPlan());

        Task target = getTargetTask(request.getTargetId(), tab);
        target.disconnect();
        Task newPrev = getPrevTask(request.getNewPrevId(), tab);
        newPrev.putInBack(target);

        return tab.getSortedTasks().stream()
            .map(Task::getId)
            .toList();
    }

    private Task getTargetTask(Long targetId, Tab tab) {
        Task target = taskRepository.findById(targetId)
            .orElseThrow(() -> new ApiException(ErrorCode.TASK_NOT_FOUND));
        Tab tabLocatedInTarget = target.getTab();
        if (!Objects.equals(tabLocatedInTarget.getPlan(), tab.getPlan())) {
            throw new ApiException(ErrorCode.AUTHORIZATION_FAIL);
        }
        return target;
    }

    private Task getPrevTask(Long prevId, Tab tab) {
        if (prevId == null) {
            return tab.getFirstDummyTask();
        }
        return taskRepository.findById(prevId)
            .orElseThrow(() -> new ApiException(ErrorCode.TASK_NOT_FOUND));
    }

    private void saveAllLabelOfTask(List<Long> labelIds, Task task, Plan plan) {
        List<LabelOfTask> labelsOfTask = labelRepository.findAllById(labelIds).stream()
            .filter(label -> Objects.equals(label.getPlan(), plan))
            .map(label -> LabelOfTask.create(label, task))
            .toList();
        labelOfTaskRepository.saveAll(labelsOfTask);
    }

    private void addEndOfTab(Task savedTask, Tab tab) {
        Task last = tab.getLastDummyTask();
        last.putInFront(savedTask);
    }
}
