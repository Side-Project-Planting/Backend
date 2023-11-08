package com.example.planservice.application;

import java.util.Objects;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.planservice.domain.member.Member;
import com.example.planservice.domain.memberofplan.MemberOfPlan;
import com.example.planservice.domain.memberofplan.repository.MemberOfPlanRepository;
import com.example.planservice.domain.plan.Plan;
import com.example.planservice.domain.plan.repository.PlanRepository;
import com.example.planservice.exception.ApiException;
import com.example.planservice.exception.ErrorCode;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PlanMembershipService {
    private final PlanRepository planRepository;
    private final MemberOfPlanRepository memberOfPlanRepository;

    public Plan getPlanAfterValidateAuthorization(Long planId, Long memberId) {
        Plan plan = planRepository.findById(planId)
            .orElseThrow(() -> new ApiException(ErrorCode.PLAN_NOT_FOUND));
        validateMemberIsInThePlan(memberId, plan);
        return plan;
    }

    public boolean validatePlanOwner(Long planId, Long memberId) {
        Plan plan = planRepository.findById(planId)
            .orElseThrow(() -> new ApiException(ErrorCode.PLAN_NOT_FOUND));

        Member owner = plan.getOwner();
        return Objects.equals(memberId, owner.getId());
    }

    public MemberOfPlan validateMemberIsInThePlan(Long memberId, Plan plan) {
        return validateMemberIsInThePlan(memberId, plan.getId());
    }

    public MemberOfPlan validateMemberIsInThePlan(Long memberId, Long planId) {
        return memberOfPlanRepository.findByPlanIdAndMemberId(planId, memberId)
            .orElseThrow(() -> new ApiException(ErrorCode.MEMBER_NOT_FOUND_IN_PLAN));
    }

    public Member getMemberBelongingToPlan(Long memberId, Plan plan) {
        MemberOfPlan memberOfPlan = validateMemberIsInThePlan(memberId, plan);
        return memberOfPlan.getMember();
    }

}
