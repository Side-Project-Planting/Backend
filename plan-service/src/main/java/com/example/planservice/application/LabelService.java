package com.example.planservice.application;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.planservice.domain.label.Label;
import com.example.planservice.domain.label.repository.LabelRepository;
import com.example.planservice.domain.memberofplan.repository.MemberOfPlanRepository;
import com.example.planservice.domain.plan.Plan;
import com.example.planservice.domain.plan.repository.PlanRepository;
import com.example.planservice.exception.ApiException;
import com.example.planservice.exception.ErrorCode;
import lombok.RequiredArgsConstructor;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class LabelService {
    private final LabelRepository labelRepository;
    private final PlanRepository planRepository;
    private final MemberOfPlanRepository memberOfPlanRepository;

    @Transactional
    public Long create(String name, Long planId, Long memberId) {
        Plan plan = planRepository.findById(planId)
            .orElseThrow(() -> new ApiException(ErrorCode.PLAN_NOT_FOUND));
        boolean isExists = memberOfPlanRepository.existsByPlanIdAndMemberId(planId, memberId);
        if (!isExists) {
            throw new ApiException(ErrorCode.MEMBER_NOT_FOUND_IN_PLAN);
        }

        Label label = Label.builder()
            .name(name)
            .plan(plan)
            .build();

        Label savedEntity = labelRepository.save(label);
        return savedEntity.getId();
    }
}
