package com.example.planservice.domain.memberofplan;

import com.example.planservice.domain.BaseEntity;
import com.example.planservice.domain.member.Member;
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
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(name = "members_of_plan")
public class MemberOfPlan extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_of_plan_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "plan_id")
    private Plan plan;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @Builder
    private MemberOfPlan(Plan plan, Member member) {
        this.plan = plan;
        this.member = member;
    }

    public static MemberOfPlan create(Member member, Plan plan) {
        return MemberOfPlan.builder()
            .member(member)
            .plan(plan)
            .build();
    }
}
