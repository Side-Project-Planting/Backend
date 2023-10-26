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

    private String profileUri;

    private String name;

    @Column(unique = true)
    private String email;

    private boolean receiveEmails;

    @Enumerated(value = EnumType.STRING)
    private Role role;

    private boolean isDeleted;

    @Builder
    private Member(String profileUri, String name, String email, boolean receiveEmails, Role role,
                   boolean isDeleted) {
        this.profileUri = profileUri;
        this.name = name;
        this.email = email;
        this.receiveEmails = receiveEmails;
        this.role = role;
        this.isDeleted = isDeleted;
    }

    @Builder(builderMethodName = "createNormalUser")
    private Member(String profileUri, String name, String email, boolean receiveEmails) {
        this.profileUri = profileUri;
        this.name = name;
        this.email = email;
        this.receiveEmails = receiveEmails;
        this.role = Role.USER;
        this.isDeleted = false;
    }
}
