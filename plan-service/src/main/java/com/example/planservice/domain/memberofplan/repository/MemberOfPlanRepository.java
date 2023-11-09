package com.example.planservice.domain.memberofplan.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.planservice.domain.memberofplan.MemberOfPlan;

@Repository
public interface MemberOfPlanRepository extends JpaRepository<MemberOfPlan, Long> {
    Optional<MemberOfPlan> findByPlanIdAndMemberId(Long planId, Long memberId);

    Optional<List<MemberOfPlan>> findAllByMemberId(Long memberId);

    void deleteByPlanIdAndMemberId(Long planId, Long memberId);

}
