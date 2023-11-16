package com.example.auth.domain.member;

import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "members")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Member {
    @Id
    @Column(name = "member_id")
    private Long id;

    private String refreshToken;

    @Builder
    private Member(Long id, String refreshToken) {
        this.id = id;
        this.refreshToken = refreshToken;
    }

    public void changeRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public boolean isRefreshTokenMatching(@NotNull String input) {
        if (input == null) {
            return false;
        }
        return Objects.equals(this.refreshToken, input);
    }
}
