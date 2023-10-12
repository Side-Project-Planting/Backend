package com.example.planservice.domain.tab;

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
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "tabs")
public class Tab extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "tab_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "plan_id")
    private Plan plan;

    private String name;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "next_id")
    private Tab next;

    @Builder
    private Tab(Plan plan, String name, Tab next) {
        this.plan = plan;
        this.name = name;
        this.next = next;
    }

    public static Tab create(Plan plan, String name) {
        return Tab.builder()
            .plan(plan)
            .name(name)
            .build();
    }

    /**
     * 오른쪽 방향으로 Tab을 연결한다
     */
    public void connect(Tab next) {
        this.next = next;
    }
}
