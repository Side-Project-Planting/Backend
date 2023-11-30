package com.example.planservice.domain.memberofplan.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.planservice.domain.memberofplan.MemberOfPlan;

@Repository
public interface MemberOfPlanRepository extends JpaRepository<MemberOfPlan, Long> {
    Optional<MemberOfPlan> findByPlanIdAndMemberId(Long planId, Long memberId);

    Optional<List<MemberOfPlan>> findAllByMemberId(Long memberId);

    void deleteByPlanIdAndMemberId(Long planId, Long memberId);

    @Modifying
    @Query("DELETE FROM MemberOfPlan m WHERE m.plan.id = :planId AND m.member.id IN :memberIds")
    void deleteAllByPlanIdAndMemberIds(@Param("planId") Long planId, @Param("memberIds") List<Long> memberIds);

    Optional<List<MemberOfPlan>> findAllByPlanId(Long id);

    boolean existsByPlanIdAndMemberId(Long planId, Long memberId);
}
