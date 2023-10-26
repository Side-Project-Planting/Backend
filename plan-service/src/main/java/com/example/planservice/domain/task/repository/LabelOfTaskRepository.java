package com.example.planservice.domain.task.repository;

import com.example.planservice.domain.task.LabelOfTask;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LabelOfTaskRepository extends JpaRepository<LabelOfTask, Long> {
}
