package com.example.planservice.domain.member.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.planservice.domain.member.Member;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {
}
