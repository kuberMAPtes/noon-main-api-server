package com.kube.noon.member.repository;

import com.kube.noon.member.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberJpaRepository extends JpaRepository<Member, String>, MemberJpaRepositoryQuery {

    Optional<Member> findMemberByNickname(String nickname);

    Optional<Member> findMemberByMemberId(String memberId);

//    List<Member> findMemberByMemberIdContainingIgnoreCase(String memberId);
//
//    List<Member> findMemberByNicknameContainingIgnoreCase(String nickname);
//
//    List<Member> findMemberByUnlockTimeBetween(LocalDateTime startTime, LocalDateTime endTime);
//
//    List<Member> findMemberByPhoneNumberContainingIgnoreCase(String phoneNumber);
//
//    List<Member> findMemberBySignOff(boolean signOff);



}
