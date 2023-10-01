package com.example.demo.domain;

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
@Table(name = "auth_members")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class OAuthMember {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String idUsingResourceServer;

    @Column(name = "type")
    @Enumerated(EnumType.STRING)
    private OAuthType type;

    private String email;

    private String profileUrl;

    private boolean isOld;

    @Builder
    private OAuthMember(String idUsingResourceServer, OAuthType oAuthType, String email, String profileUrl) {
        this.idUsingResourceServer = idUsingResourceServer;
        this.type = oAuthType;
        this.email = email;
        this.profileUrl = profileUrl;
        this.isOld = false;
    }
}
