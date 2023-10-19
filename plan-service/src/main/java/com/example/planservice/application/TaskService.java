package com.example.planservice.application;

import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.planservice.domain.member.Member;
import com.example.planservice.domain.member.repository.MemberRepository;
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
    private final MemberRepository memberRepository;
    private final PlanMembershipVerificationService planMembershipVerificationService;

    @Transactional
    public Long createTask(Long memberId, TaskCreateRequest request) {
        try {
            planMembershipVerificationService.verifyAndReturnPlan(request.getPlanId(), memberId);
            Tab tab = tabRepository.findById(request.getTabId())
                .orElseThrow(() -> new ApiException(ErrorCode.TAB_NOT_FOUND_IN_PLAN));
            Member manager = memberRepository.findById(request.getManagerId())
                .orElseThrow(() -> new ApiException(ErrorCode.MEMBER_NOT_FOUND_IN_PLAN));

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
