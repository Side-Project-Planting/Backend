package com.example.planservice.domain.task;

import com.example.planservice.domain.BaseEntity;
import com.example.planservice.domain.label.Label;
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
@Table(name = "labels_of_task")
@Getter
public class LabelOfTask extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "label_of_task_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "task_id")
    private Task task;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "label_id")
    private Label label;

    @Builder
    private LabelOfTask(Task task, Label label) {
        this.task = task;
        this.label = label;
    }

    public static LabelOfTask create(Label label, Task task) {
        return LabelOfTask.builder()
            .label(label)
            .task(task)
            .build();
    }
}
