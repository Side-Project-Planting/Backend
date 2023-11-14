package com.example.planservice.domain.tab;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.hibernate.annotations.Where;
import org.jetbrains.annotations.NotNull;

import com.example.planservice.domain.BaseEntity;
import com.example.planservice.domain.plan.Plan;
import com.example.planservice.domain.task.Task;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.persistence.Version;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "tabs",
    uniqueConstraints = {
        @UniqueConstraint(name = "UniquePlanAndTabName", columnNames = {"plan_id", "name"})
    })
@Where(clause = "is_deleted = false")
public class Tab extends BaseEntity {
    public static final int TAB_MAX_SIZE = 5;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "tab_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "plan_id")
    private Plan plan;

    private String name;

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "tab")
    private List<Task> tasks = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "next_id")
    private Tab next;

    private boolean first;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "first_task_id")
    private Task firstDummyTask;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "last_task_id")
    private Task lastDummyTask;

    @Version
    private int version;

    private boolean isDeleted;

    @Builder
    private Tab(Plan plan, String name, Tab next, boolean first, Task firstDummyTask, Task lastDummyTask,
                boolean isDeleted) {
        this.plan = plan;
        this.name = name;
        this.next = next;
        this.first = first;
        this.firstDummyTask = firstDummyTask;
        this.lastDummyTask = lastDummyTask;
        this.tasks = new ArrayList<>();
        this.isDeleted = isDeleted;
    }

    public static Tab create(Plan plan, String name) {
        return Tab.builder()
            .plan(plan)
            .name(name)
            .first(false)
            .build();
    }

    public static Tab createTodoTab(Plan plan) {
        return Tab.builder()
            .plan(plan)
            .name("TODO")
            .first(true)
            .build();
    }

    /**
     * 오른쪽 방향으로 Tab을 연결한다
     */
    public void connect(Tab next) {
        this.next = next;
    }

    public void changeName(@NotNull String name) {
        // TODO Tab 이름에 대한 제약조건 이야기해보기
        this.name = name;
    }

    public void setFirstDummyTask(Task firstDummyTask) {
        if (this.firstDummyTask != null) {
            throw new IllegalArgumentException("한 번 초기화된 firstDummyTask는 변경할 수 없습니다");
        }
        tasks.add(firstDummyTask);
        this.firstDummyTask = firstDummyTask;
    }

    public void setLastDummyTask(Task lastDummyTask) {
        if (this.lastDummyTask != null) {
            throw new IllegalArgumentException("한 번 초기화된 LastDummyTask는 변경할 수 없습니다");
        }
        tasks.add(lastDummyTask);
        this.lastDummyTask = lastDummyTask;
    }

    public void delete() {
        this.isDeleted = true;
        tasks.forEach(Task::delete);
    }

    public List<Task> getSortedTasks() {
        List<Task> result = new ArrayList<>();
        Task temp = firstDummyTask;
        while (!Objects.equals(lastDummyTask, temp.getNext())) {
            temp = temp.getNext();
            result.add(temp);
        }
        return result;
    }

}
