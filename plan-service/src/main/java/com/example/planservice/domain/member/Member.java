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

@Entity
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

}
