package com.example.planservice.domain.plan;

import org.hibernate.annotations.Where;

import com.example.planservice.domain.BaseEntity;
import com.example.planservice.domain.member.Member;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "plans")
@Where(clause = "is_deleted = false")
public class Plan extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "plan_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id")
    private Member owner;

    private String title;

    private String intro;

    private boolean isPublic;

    private int starCnt;

    private int viewCnt;

    private boolean isDeleted;
}
