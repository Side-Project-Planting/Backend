package com.example.planservice.domain.tab;

import org.jetbrains.annotations.NotNull;

import com.example.planservice.domain.BaseEntity;
import com.example.planservice.domain.plan.Plan;
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

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "tabs")
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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "next_id")
    private Tab next;

    private boolean first;

    @Version
    private int version;

    @Builder
    private Tab(Plan plan, String name, Tab next, boolean first) {
        this.plan = plan;
        this.name = name;
        this.next = next;
        this.first = first;
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

    public void makeNotFirst() {
        this.first = false;
    }

    public void changeName(@NotNull String name) {
        // TODO Tab 이름에 대한 제약조건 이야기해보기
        this.name = name;
    }
}
