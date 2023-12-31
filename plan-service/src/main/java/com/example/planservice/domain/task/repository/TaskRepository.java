package com.example.planservice.domain.task.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.example.planservice.domain.task.Task;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> findAllByTabId(Long tabId);

    @Modifying
    @Query("update Task t set t.isDeleted = true where t.tab.id = :tabId")
    void deleteAllByTabId(Long tabId);
}
