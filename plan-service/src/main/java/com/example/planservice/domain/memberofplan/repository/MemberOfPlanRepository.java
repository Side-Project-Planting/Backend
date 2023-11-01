package com.example.planservice.domain.memberofplan.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.planservice.domain.memberofplan.MemberOfPlan;

import java.util.Optional;

@Repository
public interface MemberOfPlanRepository extends JpaRepository<MemberOfPlan, Long> {
    Optional<MemberOfPlan> findByPlanIdAndMemberId(Long planId, Long memberId);

}
