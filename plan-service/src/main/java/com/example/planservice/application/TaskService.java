package com.example.planservice.application;

import java.util.List;
import java.util.Objects;

import org.springframework.orm.ObjectOptimisticLockingFailureException;
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
        try {
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
            Task last = tab.getLastDummyTask();
            last.putInFront(savedTask);
            return savedTask.getId();
        } catch (ObjectOptimisticLockingFailureException e) {
            throw new ApiException(ErrorCode.REQUEST_CONFLICT);
        }
    }

    @Transactional
    public List<Long> changeOrder(Long memberId, TaskChangeOrderRequest request) {
        Tab tab = tabRepository.findById(request.getTargetTabId())
            .orElseThrow(() -> new ApiException(ErrorCode.TAB_NOT_FOUND_IN_PLAN));
        planMembershipService.validateMemberIsInThePlan(memberId, tab.getPlan());

        Task target = taskRepository.findById(request.getTargetId())
            .orElseThrow(() -> new ApiException(ErrorCode.TASK_NOT_FOUND));
        if (!Objects.equals(target.getTab().getPlan(), tab.getPlan())) {
            throw new ApiException(ErrorCode.AUTHORIZATION_FAIL);
        }
        target.disconnect();

        Task newPrev;
        if (request.getNewPrevId() == null) {
            newPrev = tab.getFirstDummyTask();
        } else {
            newPrev = taskRepository.findById(request.getNewPrevId())
                .orElseThrow(() -> new ApiException(ErrorCode.TASK_NOT_FOUND));
        }
        newPrev.putInBack(target);
        return tab.getSortedTasks().stream()
            .map(Task::getId)
            .toList();
    }

    private void saveAllLabelOfTask(List<Long> labelIds, Task task, Plan plan) {
        List<LabelOfTask> labelsOfTask = labelRepository.findAllById(labelIds).stream()
            .filter(label -> Objects.equals(label.getPlan(), plan))
            .map(label -> LabelOfTask.create(label, task))
            .toList();
        labelOfTaskRepository.saveAll(labelsOfTask);
    }

}
