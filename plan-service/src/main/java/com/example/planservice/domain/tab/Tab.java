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

    @OneToOne(fetch = FetchType.LAZY)
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
            .first(true)
            .build();
    }

    /**
     * 오른쪽 방향으로 Tab을 연결한다
     */
    public void connect(Tab next) {
        this.next = next;
    }

    // TODO Plan이 하나 생성되면 무조건 투두 탭과 Done 탭을 가져야 함
    //  사실상 투두 탭 때문에 존재하는 속성인데, 투두탭에 관련한 생성자를 만드는 게 더 바람직할듯
    public void makeNotFirst() {
        this.first = false;
    }
}
