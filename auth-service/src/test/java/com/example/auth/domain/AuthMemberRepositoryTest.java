package com.example.auth.domain;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
class AuthMemberRepositoryTest {
    @Autowired
    AuthMemberRepository authMemberRepository;

    @Test
    @DisplayName("리소스서버의 id와 OAuthType을 사용해 조회한다")
    void getOAuthMemberUsingIdUsingResourceServerAndType() {
        // given
        final String idUsingResourceServer = "1234";
        final OAuthType type = OAuthType.GOOGLE;

        final OAuthInfo oAuthInfo = OAuthInfo.builder()
                                                   .email("hello@naver.com")
                                                   .profileUrl("https://imageurl")
                                                   .idUsingResourceServer(idUsingResourceServer)
                                                   .oAuthType(OAuthType.GOOGLE)
                                                   .build();
        authMemberRepository.save(oAuthInfo);

        // when
        final OAuthInfo result =
                authMemberRepository.findByIdUsingResourceServerAndType(idUsingResourceServer, type).get();

        // then
        assertThat(result.getId()).isNotNull();
        assertThat(result.getEmail()).isEqualTo("hello@naver.com");
        assertThat(result.getProfileUrl()).isEqualTo("https://imageurl");
        assertThat(result.getIdUsingResourceServer()).isEqualTo(idUsingResourceServer);
        assertThat(result.getType()).isEqualTo(OAuthType.GOOGLE);
    }

    @Test
    @DisplayName("리소스서버의 id와 OAuthType을 사용해 조회할 경우, 해당되는 값이 없으면 비어있는 Optional.empty()를 반환한다")
    void get() {
        // given
        final String idUsingResourceServer = "1234";
        final OAuthType type = OAuthType.GOOGLE;

        // when
        final Optional<OAuthInfo> resultOpt =
                authMemberRepository.findByIdUsingResourceServerAndType(idUsingResourceServer, type);

        // then
        assertThat(resultOpt).isEmpty();
    }
}
