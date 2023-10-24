package com.example.planservice.application;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.planservice.domain.label.Label;
import com.example.planservice.domain.label.repository.LabelRepository;
import com.example.planservice.domain.memberofplan.repository.MemberOfPlanRepository;
import com.example.planservice.domain.plan.Plan;
import com.example.planservice.domain.plan.repository.PlanRepository;
import com.example.planservice.exception.ApiException;
import com.example.planservice.exception.ErrorCode;
import com.example.planservice.presentation.dto.request.LabelCreateRequest;
import lombok.RequiredArgsConstructor;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class LabelService {
    private final LabelRepository labelRepository;
    private final PlanRepository planRepository;
    private final MemberOfPlanRepository memberOfPlanRepository;

    @Transactional
    public Long create(Long memberId, LabelCreateRequest request) {
        Long planId = request.getPlanId();
        String name = request.getName();
        Plan plan = planRepository.findById(planId)
            .orElseThrow(() -> new ApiException(ErrorCode.PLAN_NOT_FOUND));

        boolean existMemberInPlan = memberOfPlanRepository.existsByPlanIdAndMemberId(planId, memberId);
        if (!existMemberInPlan) {
            throw new ApiException(ErrorCode.MEMBER_NOT_FOUND_IN_PLAN);
        }
        if (plan.existsDuplicatedLabelName(name)) {
            throw new ApiException(ErrorCode.LABEL_NAME_DUPLICATE);
        }

        Label label = Label.create(name, plan);
        Label savedEntity = labelRepository.save(label);

        try {
            labelRepository.flush();
        } catch (DataIntegrityViolationException e) {
            throw new ApiException(ErrorCode.REQUEST_CONFLICT);
        }
        return savedEntity.getId();
    }
}
