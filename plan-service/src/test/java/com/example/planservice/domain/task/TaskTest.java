package com.example.planservice.domain.task;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.LocalDateTime;

import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.example.planservice.domain.tab.Tab;
import com.example.planservice.exception.ApiException;
import com.example.planservice.exception.ErrorCode;

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

    @Test
    @DisplayName("태스크의 startDate는 endDate보다 빨라야 합니다")
    void testDateIsValid() throws Exception {
        // given
        LocalDateTime endDate = LocalDateTime.now();
        LocalDateTime startDate = endDate.plusDays(1);

        // when & then
        assertThatThrownBy(() -> createTask(startDate, endDate))
            .isInstanceOf(ApiException.class)
            .hasMessageContaining(ErrorCode.TASK_DATE_INVALID.getMessage());
    }

    private Task createTask(LocalDateTime startDate, LocalDateTime endDate) {
        return Task.builder()
            .startDate(startDate)
            .endDate(endDate)
            .build();
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