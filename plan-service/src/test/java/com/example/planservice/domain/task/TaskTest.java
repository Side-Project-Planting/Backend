package com.example.planservice.domain.task;

import static org.assertj.core.api.Assertions.assertThat;

import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.example.planservice.domain.tab.Tab;

class TaskTest {
    @Test
    @DisplayName("태스크를 서로 연결한다")
    void connect() {
        // given
        Tab tab = createTab();

        Task task1 = createTask(tab);
        createTask(tab);
        Task newTask = Task.builder().build();

        // when
        task1.putInBack(newTask);

        // then
        assertThat(task1.getNext()).isEqualTo(newTask);
        assertThat(newTask.getPrev()).isEqualTo(task1);
    }

    @NotNull
    private static Task createTask(Tab tab) {
        Task task = Task.builder()
            .next(null)
            .tab(tab)
            .build();

        Task lastDummyTask = tab.getLastDummyTask();
        lastDummyTask.putInFront(task);
        return task;
    }

    private Tab createTab() {
        Tab tab = Tab.builder().build();
        Task.createFirstAndLastDummy(tab);
        return tab;
    }
}