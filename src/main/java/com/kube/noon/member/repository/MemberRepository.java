package com.kube.noon.member.repository;

import com.kube.noon.member.domain.Member;
import com.kube.noon.member.domain.MemberRelationship;

import java.util.Optional;

public interface MemberRepository {

    void addMember(Member member);//JPA

    void addMemberRelationship(MemberRelationship memberRelationship);

    Optional<Member> findMemberById(String memberId);

    Optional<Member> findMemberByNickname(String nickname);//JPA

    void updateMember(Member member);

    void updatePassword(String memberId, String newPassword);

    void updatePhoneNumber(String memberId,String newPassword);

    void updateMemberProfilePhoto(String memberId, String newProfilePhotoUrl);

    void deleteMemberRelationship(int memberRelationshipId);

    void deleteMember(String memberId);
}