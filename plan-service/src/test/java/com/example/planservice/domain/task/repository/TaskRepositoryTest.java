package com.example.planservice.domain.task.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.Transactional;

import com.example.planservice.config.TestConfig;
import com.example.planservice.domain.tab.Tab;
import com.example.planservice.domain.tab.repository.TabRepository;
import com.example.planservice.domain.task.Task;

@SpringBootTest
@Import(TestConfig.class)
@Transactional
class TaskRepositoryTest {
    @Autowired
    TaskRepository taskRepository;

    @Autowired
    TabRepository tabRepository;

    @Test
    @DisplayName("TabId에 해당하는 모든 태스크를 가져온다")
    void findAllByTabId() {
        // given
        Tab tab = createTab();
        Tab otherTab = createTab();
        Task task1 = createTask(tab);
        Task task2 = createTask(tab);
        Task otherTask = createTask(otherTab);

        // when
        List<Task> result = taskRepository.findAllByTabId(tab.getId());

        // then
        assertThat(result).hasSize(2)
            .contains(task1, task2);
    }

    private Tab createTab() {
        Tab tab = Tab.builder()
            .build();
        tabRepository.save(tab);
        return tab;
    }

    private Task createTask(Tab tab) {
        Task task = Task.builder()
            .tab(tab)
            .build();
        taskRepository.save(task);
        return task;
    }
}
