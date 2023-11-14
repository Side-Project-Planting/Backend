package com.example.auth.domain;

import java.util.Objects;

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
import lombok.Setter;

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

    @Column
    private Long memberId;

    // TODO 추후 Redis로 이동
    @Setter
    private String authorizedToken;

    @Builder
    private OAuthInfo(String idUsingResourceServer, OAuthType oAuthType, String email, Long memberId,
                      String authorizedToken) {
        this.idUsingResourceServer = idUsingResourceServer;
        this.type = oAuthType;
        this.email = email;
        this.memberId = memberId;
        this.authorizedToken = authorizedToken;
    }

    /**
     * 이후에 MemberId Member랑 매핑할거야
     */
    public void init(Long memberId) {
        this.memberId = memberId;
    }

    public boolean validateAuthorizedToken(@NotNull String authorizedToken) {
        return Objects.equals(this.authorizedToken, authorizedToken);
    }
}
