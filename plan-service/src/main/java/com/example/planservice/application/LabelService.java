package com.example.planservice.application;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.planservice.application.dto.LabelDeleteServiceRequest;
import com.example.planservice.domain.label.Label;
import com.example.planservice.domain.label.repository.LabelRepository;
import com.example.planservice.domain.plan.Plan;
import com.example.planservice.exception.ApiException;
import com.example.planservice.exception.ErrorCode;
import com.example.planservice.presentation.dto.request.LabelCreateRequest;
import lombok.RequiredArgsConstructor;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class LabelService {
    private final LabelRepository labelRepository;
    private final PlanMembershipService planMembershipService;

    @Transactional
    public Long create(Long memberId, LabelCreateRequest request) {
        Plan plan = planMembershipService.getPlanAfterValidateAuthorization(request.getPlanId(), memberId);

        String name = request.getName();
        if (plan.existsDuplicatedLabelName(name)) {
            throw new ApiException(ErrorCode.LABEL_NAME_DUPLICATE);
        }
        Label label = Label.create(name, plan);
        Label savedEntity = labelRepository.save(label);
        return savedEntity.getId();
    }

    @Transactional
    public void delete(LabelDeleteServiceRequest request) {
        Plan plan = planMembershipService.getPlanAfterValidateAuthorization(request.getPlanId(), request.getMemberId());
        Label label = labelRepository.findById(request.getLabelId())
            .orElseThrow(() -> new ApiException(ErrorCode.LABEL_NOT_FOUND));
        label.validateBelongsToPlan(plan);

        labelRepository.delete(label);
        plan.remove(label);
    }
}
