package com.example.repository.member;

import com.example.domain.member.Member;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {

    // Id(PK)로 회원 단건 조회
    Optional<Member> findById(Long memberId);

    // userId로 회원 단건 조회
    Optional<Member> findByUserId(@Param("email") String userId);

    // email로 회원 단건 조회
    Optional<Member> findByEmail(@Param("email") String email);


}
