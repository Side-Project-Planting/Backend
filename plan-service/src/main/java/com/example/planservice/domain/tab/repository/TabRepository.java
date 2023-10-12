package com.example.planservice.domain.tab.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.planservice.domain.tab.Tab;

import java.util.List;

@Repository
public interface TabRepository extends JpaRepository<Tab, Long> {
    List<Tab> findAllByPlanId(Long planId);
}
