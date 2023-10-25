package com.example.planservice.domain.label;

import com.example.planservice.domain.BaseEntity;
import com.example.planservice.domain.plan.Plan;
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
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Objects;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(name = "labels",
    uniqueConstraints = {
        @UniqueConstraint(name = "UniquePlanAndLabelName", columnNames = {"plan_id", "name"})
    })
public class Label extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "label_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "plan_id")
    private Plan plan;

    private String name;

    @Builder
    private Label(Plan plan, String name) {
        this.plan = plan;
        this.name = name;
    }

    public static Label create(String name, Plan plan) {
        return Label.builder()
            .name(name)
            .plan(plan)
            .build();
    }

    public void validateBelongsToPlan(Plan plan) {
        if (!Objects.equals(this.plan.getId(), plan.getId())) {
            throw new ApiException(ErrorCode.AUTHORIZATION_FAIL);
        }
    }
}
