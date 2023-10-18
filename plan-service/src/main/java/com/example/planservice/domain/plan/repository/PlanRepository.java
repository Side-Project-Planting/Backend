package com.example.planservice.domain.plan.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.planservice.domain.plan.Plan;

@Repository
public interface PlanRepository extends JpaRepository<Plan, Long> {
}
