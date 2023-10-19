package com.example.planservice.domain.task;

import static org.assertj.core.api.Assertions.assertThat;

import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class TaskTest {
    @Test
    @DisplayName("태스크를 서로 연결한다")
    void connect() {
        // given
        Task task1 = createTask(null);
        createTask(task1);
        Task newTask = Task.builder().build();

        // when
        task1.connect(newTask);

        // then
        assertThat(task1.getNext()).isEqualTo(newTask);
        assertThat(newTask.getPrev()).isEqualTo(task1);
    }

    @NotNull
    private static Task createTask(Task prev) {
        Task task2 = Task.builder()
            .next(null)
            .prev(prev)
            .build();
        if (prev != null) {
            prev.setNext(task2);
        }
        return task2;
    }

}