package com.example.planservice.application;

import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.planservice.domain.member.Member;
import com.example.planservice.domain.tab.Tab;
import com.example.planservice.domain.tab.repository.TabRepository;
import com.example.planservice.domain.task.Task;
import com.example.planservice.domain.task.repository.TaskRepository;
import com.example.planservice.exception.ApiException;
import com.example.planservice.exception.ErrorCode;
import com.example.planservice.presentation.dto.request.TaskCreateRequest;
import lombok.RequiredArgsConstructor;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class TaskService {
    private final TaskRepository taskRepository;
    private final TabRepository tabRepository;
    private final PlanMembershipService planMembershipService;

    @Transactional
    public Long create(Long memberId, TaskCreateRequest request) {
        try {
            Tab tab = tabRepository.findById(request.getTabId())
                .orElseThrow(() -> new ApiException(ErrorCode.TAB_NOT_FOUND_IN_PLAN));
            planMembershipService.verifyMemberIsInThePlan(memberId, tab.getPlan());
            Member manager = planMembershipService.getMemberBelongingToPlan(request.getManagerId(), tab.getPlan());

            Task task = Task.builder()
                .tab(tab)
                .manager(manager)
                .name(request.getName())
                .description(request.getDescription())
                .build();
            Task oldLastTask = tab.makeLastTask(task);
            if (oldLastTask != null) {
                oldLastTask.connect(task);
            }

            Task savedTask = taskRepository.save(task);
            return savedTask.getId();
        } catch (ObjectOptimisticLockingFailureException e) {
            throw new ApiException(ErrorCode.REQUEST_CONFLICT);
        }
    }

}
