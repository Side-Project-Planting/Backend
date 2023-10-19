package com.example.planservice.application;

import org.springframework.stereotype.Service;

import com.example.planservice.domain.memberofplan.repository.MemberOfPlanRepository;
import com.example.planservice.domain.plan.Plan;
import com.example.planservice.domain.plan.repository.PlanRepository;
import com.example.planservice.exception.ApiException;
import com.example.planservice.exception.ErrorCode;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PlanMembershipVerificationService {
    private final PlanRepository planRepository;
    private final MemberOfPlanRepository memberOfPlanRepository;

    public Plan verifyAndReturnPlan(Long planId, Long memberId) {
        Plan plan = planRepository.findById(planId)
            .orElseThrow(() -> new ApiException(ErrorCode.PLAN_NOT_FOUND));

        boolean existsInPlan = memberOfPlanRepository.existsByPlanIdAndMemberId(plan.getId(), memberId);
        if (!existsInPlan) {
            throw new ApiException(ErrorCode.MEMBER_NOT_FOUND_IN_PLAN);
        }
        return plan;
    }
}
