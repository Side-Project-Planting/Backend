package com.example.planservice.domain.memberofplan.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.planservice.domain.memberofplan.MemberOfPlan;

@Repository
public interface MemberOfPlanRepository extends JpaRepository<MemberOfPlan, Long> {
    boolean existsByPlanIdAndMemberId(Long planId, Long memberId);
}
