package com.example.auth.domain;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AuthMemberRepository extends JpaRepository<OAuthInfo, Long> {
    Optional<OAuthInfo> findByIdUsingResourceServerAndType(String idUsingResourceServer, OAuthType type);
}
