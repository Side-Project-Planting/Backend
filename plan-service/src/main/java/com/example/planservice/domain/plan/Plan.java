package com.example.planservice.domain.plan;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.hibernate.annotations.Where;

import com.example.planservice.domain.BaseEntity;
import com.example.planservice.domain.label.Label;
import com.example.planservice.domain.member.Member;
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
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "plans")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
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

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "plan")
    private List<Label> labels = new ArrayList<>();

    @Builder
    private Plan(Member owner, String title, String intro, boolean isPublic, int starCnt, int viewCnt,
                 boolean isDeleted) {
        this.owner = owner;
        this.title = title;
        this.intro = intro;
        this.isPublic = isPublic;
        this.starCnt = starCnt;
        this.viewCnt = viewCnt;
        this.isDeleted = isDeleted;
    }

    public boolean existsDuplicatedLabelName(String name) {
        return labels.stream()
            .anyMatch(label -> Objects.equals(label.getName(), name));
    }

    public void remove(Label label) {
        labels.remove(label);
    }

    public void addLabel(Label label) {
        labels.add(label);
    }
}
