package com.example.planservice.domain.task.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.planservice.domain.task.LabelOfTask;

@Repository
public interface LabelOfTaskRepository extends JpaRepository<LabelOfTask, Long> {
    List<LabelOfTask> findAllByTaskId(Long id);
}
