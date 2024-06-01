package com.kube.noon.member.repository;

import com.kube.noon.member.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface MemberJpaRepository extends JpaRepository<Member, String> {

    public Optional<Member> findMemberByNickname(String nickname);

    public Optional<Member> findMemberById(String memberId);

    public List<Member> findMemberByMemberIdContainingIgnoreCase(String memberId);

    public List<Member> findMemberByNicknameContainingIgnoreCase(String nickname);

    public List<Member> findMemberByUnlockTimeBetween(LocalDateTime startTime, LocalDateTime endTime);

    public List<Member> findMemberByPhoneNumberContainingIgnoreCase(String phoneNumber);

    public List<Member> findMemberBySignOff(boolean signOff);



}
