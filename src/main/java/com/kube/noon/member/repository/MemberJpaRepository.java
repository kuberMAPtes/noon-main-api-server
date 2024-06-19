package com.kube.noon.member.repository;

import com.kube.noon.member.domain.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface MemberJpaRepository extends JpaRepository<Member, String>, MemberJpaRepositoryQuery {

    Optional<Member> findMemberByNickname(String nickname);

    Optional<Member> findMemberByMemberId(String memberId);

    Optional<Member> findMemberByPhoneNumber(String phoneNumber);

    @Query("""
            SELECT m FROM Member m
            WHERE m.nickname LIKE CONCAT('%', :nickname, '%')
                AND m.unlockTime < NOW()
                AND m.signedOff = FALSE
                AND m.memberId != :requester
            """)
    Page<Member> findMemberByNicknameLike(String nickname, String requester, Pageable pageable);
}
