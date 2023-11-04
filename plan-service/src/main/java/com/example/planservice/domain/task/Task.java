package com.example.planservice.domain.task;

import java.time.LocalDateTime;

import org.hibernate.annotations.Where;

import com.example.planservice.domain.BaseEntity;
import com.example.planservice.domain.member.Member;
import com.example.planservice.domain.tab.Tab;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
    @JoinColumn(name = "manager_id")
    private Member manager;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "writer_id")
    private Member writer;

    private String name;

    private String description;

    private LocalDateTime startDate;

    private LocalDateTime endDate;

    private boolean isDeleted;

    @Setter
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
    public Task(Tab tab, Member manager, Member writer, String name, String description, LocalDateTime startDate,
                LocalDateTime endDate, boolean isDeleted, Task next, Task prev, int version) {
        this.tab = tab;
        this.manager = manager;
        this.writer = writer;
        this.name = name;
        this.description = description;
        this.startDate = startDate;
        this.endDate = endDate;
        this.isDeleted = isDeleted;
        this.next = next;
        this.prev = prev;
        this.version = version;
    }

    public static Task createFirstDummy(Tab tab) {
        return createDummy(tab, FIRST_DUMMY_NAME);
    }

    public static Task createLastDummy(Tab tab) {
        return createDummy(tab, LAST_DUMMY_NAME);
    }

    public void connect(Task next) {
        if (next == null) {
            this.next = null;
            return;
        }

        this.next = next;
        next.prev = this;
    }

    public void delete() {
        this.isDeleted = true;
    }

    private static Task createDummy(Tab tab, String name) {
        return Task.builder()
            .tab(tab)
            .name(name)
            .build();
    }
}
