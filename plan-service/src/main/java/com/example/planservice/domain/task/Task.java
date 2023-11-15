package com.example.planservice.domain.task;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.Where;
import org.jetbrains.annotations.NotNull;

import com.example.planservice.domain.BaseEntity;
import com.example.planservice.domain.member.Member;
import com.example.planservice.domain.tab.Tab;
import com.example.planservice.exception.ApiException;
import com.example.planservice.exception.ErrorCode;
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
import jakarta.persistence.Version;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "tasks")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Where(clause = "is_deleted = false")
@Getter
public class Task extends BaseEntity {
    public static final String FIRST_DUMMY_NAME = "first";
    public static final String LAST_DUMMY_NAME = "last";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "task_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tab_id")
    private Tab tab;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assignee_id")
    private Member assignee;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "task")
    private List<LabelOfTask> labelOfTasks = new ArrayList<>();

    private String name;

    private String description;

    private LocalDateTime startDate;

    private LocalDateTime endDate;

    private boolean isDeleted;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "next_id")
    private Task next;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "prev_id")
    private Task prev;

    @Version
    private int version;

    @Builder
    @SuppressWarnings("java:S107")
    private Task(Tab tab, Member assignee, String name, String description, LocalDateTime startDate,
                 LocalDateTime endDate, boolean isDeleted, Task next, Task prev, int version) {
        validateDates(startDate, endDate);
        this.tab = tab;
        this.assignee = assignee;
        this.name = name;
        this.description = description;
        this.startDate = startDate;
        this.endDate = endDate;
        this.isDeleted = isDeleted;
        this.next = next;
        this.prev = prev;
        this.version = version;
    }

    public static List<Task> createFirstAndLastDummy(Tab tab) {
        Task firstDummy = createDummy(tab, FIRST_DUMMY_NAME);
        Task lastDummy = createDummy(tab, LAST_DUMMY_NAME);

        firstDummy.putInBack(lastDummy);

        tab.setFirstDummyTask(firstDummy);
        tab.setLastDummyTask(lastDummy);
        return List.of(firstDummy, lastDummy);
    }

    public void putInBack(@NotNull Task target) {
        Task originalNext = this.next;
        if (originalNext == null) {
            this.next = target;
            target.prev = this;
            return;
        }
        originalNext.prev = target;
        target.next = originalNext;

        target.prev = this;
        this.next = target;
        tab.getTasks()
            .add(target);
    }

    public void putInFront(@NotNull Task target) {
        if (target.isConnected()) {
            throw new IllegalArgumentException("target은 연결이 되어 있어서는 안됩니다.");
        }
        Task originalPrev = this.prev;
        if (originalPrev == null) {
            this.prev = target;
            target.next = this;
            return;
        }
        originalPrev.next = target;
        target.prev = originalPrev;

        target.next = this;
        this.prev = target;
        tab.getTasks()
            .add(target);
    }

    public void disconnect() {
        validateCanModify();

        Task originalPrev = this.prev;
        Task originalNext = this.next;
        originalNext.setPrev(originalPrev);
        originalPrev.setNext(originalNext);

        this.prev = null;
        this.next = null;
        tab.getTasks()
            .remove(this);
    }

    public void validateCanModify() {
        Task firstDummyTask = tab.getFirstDummyTask();
        if (this.getId()
            .equals(firstDummyTask.getId())) {
            throw new ApiException(ErrorCode.TASK_NOT_FOUND);
        }
        Task lastDummyTask = tab.getLastDummyTask();
        if (this.getId()
            .equals(lastDummyTask.getId())) {
            throw new ApiException(ErrorCode.TASK_NOT_FOUND);
        }
    }

    public void change(Task entity) {
        validateCanModify();

        this.assignee = entity.getAssignee();
        this.name = entity.getName();
        this.description = entity.getDescription();
        this.startDate = entity.getStartDate();
        this.endDate = entity.getEndDate();
    }

    public void setNext(Task next) {
        this.next = next;
    }

    public void setPrev(Task prev) {
        this.prev = prev;
    }

    public void delete() {
        validateCanModify();
        disconnect();
        this.isDeleted = true;
    }

    private static Task createDummy(Tab tab, String name) {
        return Task.builder()
            .tab(tab)
            .name(name)
            .build();
    }

    private boolean isConnected() {
        return next != null || prev != null;
    }

    private void validateDates(LocalDateTime startDate, LocalDateTime endDate) {
        if (startDate == null || endDate == null) {
            return;
        }

        if (startDate.isAfter(endDate)) {
            throw new ApiException(ErrorCode.TASK_DATE_INVALID);
        }
    }

    public boolean isDummy() {
        return this.name.equals(FIRST_DUMMY_NAME) || this.name.equals(LAST_DUMMY_NAME);
    }

}
