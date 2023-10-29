package com.example.auth.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Objects;

@Entity
@Table(name = "oauth_infos")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class OAuthInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String idUsingResourceServer;

    @Column(name = "type")
    @Enumerated(EnumType.STRING)
    private OAuthType type;

    private String email;

    private String profileUrl;

    private String refreshToken;

    private boolean registered;

    @Builder
    private OAuthInfo(String idUsingResourceServer, OAuthType oAuthType, String email, String profileUrl,
                      String refreshToken) {
        this.idUsingResourceServer = idUsingResourceServer;
        this.type = oAuthType;
        this.email = email;
        this.profileUrl = profileUrl;
        this.refreshToken = refreshToken;
        this.registered = false;
    }

    public void init(String profileUrl) {
        this.profileUrl = profileUrl;
        this.registered = true;
    }

    public void changeRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public boolean isRefreshTokenMatching(@NotNull String input) {
        return Objects.equals(this.refreshToken, input);
    }
}
