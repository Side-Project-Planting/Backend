package com.example.planservice.domain.member;

import org.hibernate.annotations.Where;

import com.example.planservice.domain.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "members")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Where(clause = "is_deleted = false")
public class Member extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    private Long id;

    private String profileImgUri;

    private String name;

    private String email;

    private boolean receiveEmails;

    @Enumerated(value = EnumType.STRING)
    private Role role;

    private boolean isDeleted;

    @Builder
    private Member(String profileImgUri, String name, String email, boolean receiveEmails, Role role,
                   boolean isDeleted) {
        this.profileImgUri = profileImgUri;
        this.name = name;
        this.email = email;
        this.receiveEmails = receiveEmails;
        this.role = role;
        this.isDeleted = isDeleted;
    }
}
