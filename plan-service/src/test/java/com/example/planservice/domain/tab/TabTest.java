package com.example.planservice.domain.tab;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.example.planservice.domain.plan.Plan;
import com.example.planservice.domain.task.Task;

class TabTest {
    @Test
    @DisplayName("탭을 생성한다")
    void createTab() {
        // given
        Plan plan = Plan.builder().build();

        // when
        Tab tab = Tab.create(plan, "탭이름");

        // then
        assertThat(tab.getName()).isEqualTo("탭이름");
        assertThat(tab.getNext()).isNull();
        assertThat(tab.isFirst()).isFalse();
        assertThat(tab.getPlan()).isEqualTo(plan);
    }

    @Test
    @DisplayName("첫 번째 순서인 탭을 생성한다")
    void createFirstTab() {
        // given
        Plan plan = Plan.builder().build();

        // when
        Tab tab = Tab.createTodoTab(plan);

        // then
        assertThat(tab.getName()).isEqualTo("TODO");
        assertThat(tab.getNext()).isNull();
        assertThat(tab.isFirst()).isTrue();
        assertThat(tab.getPlan()).isEqualTo(plan);
    }

    @Test
    @DisplayName("탭은 오른쪽 방향으로 연결된다")
    void connectTab() {
        // given
        Plan plan = Plan.builder().build();
        Tab oldTab = Tab.create(plan, "탭이름");
        Tab newTab = Tab.create(plan, "새로운탭");

        // when
        oldTab.connect(newTab);

        assertThat(oldTab.getNext()).isEqualTo(newTab);
    }

    @Test
    @DisplayName("이름을 변경한다")
    void changeName() {
        // given
        Plan plan = Plan.builder().build();
        Tab tab = Tab.create(plan, "탭이름");

        // when
        tab.changeName("이름변경");

        // then
        assertThat(tab.getName()).isEqualTo("이름변경");
    }

    @Test
    @DisplayName("탭의 마지막 태스크를 변경한다")
    void testChangeLastTask() {
        // given
        Task originalLastTask = Task.builder().build();
        Tab tab = Tab.builder()
            .lastDummyTask(originalLastTask)
            .build();
        Task task = Task.builder().build();

        // when
        tab.changeLastTask(task);

        // then
        assertThat(tab.getLastDummyTask()).isEqualTo(task);
        assertThat(originalLastTask.getNext()).isEqualTo(task);
        assertThat(task.getNext()).isNull();
    }
}
