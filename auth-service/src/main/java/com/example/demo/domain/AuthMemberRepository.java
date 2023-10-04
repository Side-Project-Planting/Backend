package com.example.demo.domain;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AuthMemberRepository extends JpaRepository<OAuthMember, Long> {
    Optional<OAuthMember> findByIdUsingResourceServerAndType(String idUsingResourceServer, OAuthType type);
}
