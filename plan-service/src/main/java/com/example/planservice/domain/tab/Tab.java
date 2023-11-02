package com.example.planservice.domain.tab;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.Where;
import org.jetbrains.annotations.NotNull;

import com.example.planservice.domain.BaseEntity;
import com.example.planservice.domain.Linkable;
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
@Table(name = "tabs", uniqueConstraints = @UniqueConstraint(name = "UniquePlanAndTabName", columnNames = {"plan_id",
    "name"}))
@Where(clause = "is_deleted = false")
public class Tab extends BaseEntity implements Linkable<Tab> {
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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "last_task_id")
    private Task lastTask;

    @Version
    private int version;

    private boolean isDeleted;

    @Builder
    private Tab(Plan plan, String name, Tab next, boolean first, Task lastTask, List<Task> tasks, boolean isDeleted) {
        this.plan = plan;
        this.name = name;
        this.next = next;
        this.first = first;
        this.lastTask = lastTask;
        this.tasks = tasks;
        this.isDeleted = isDeleted;
    }

    public static Tab create(Plan plan, String name) {
        return builder()
            .plan(plan)
            .name(name)
            .first(false)
            .build();
    }

    public static Tab createTodoTab(Plan plan) {
        return builder()
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

    public void makeNotFirst() {
        first = false;
    }

    public void changeName(@NotNull String name) {
        // TODO Tab 이름에 대한 제약조건 이야기해보기
        this.name = name;
    }

    public void changeLastTask(Task task) {
        if (lastTask != null) {
            lastTask.connect(task);
        }
        lastTask = task;
    }

    public void delete() {
        isDeleted = true;
        tasks.forEach(Task::delete);
    }
}
