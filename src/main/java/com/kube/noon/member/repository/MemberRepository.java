package com.kube.noon.member.repository;

import com.kube.noon.member.domain.MemberRelationship;
import com.kube.noon.member.dto.MemberRelationshipSearchCriteriaDto;
import com.kube.noon.member.dto.MemberSearchCriteriaDto;

import java.util.List;
import java.util.Optional;

/**
 * 서비스레이어만들때 주의할 점
 * 다정온도max,제한
 * findMemberRelationshipListByCriteria는 멤버도 리턴해야돼
 */
public interface MemberRepository {

    void addMember(com.kube.noon.member.domain.Member member);

    void addMemberRelationship(MemberRelationship memberRelationship);

    Optional<com.kube.noon.member.domain.Member> findMemberById(String memberId);

    Optional<com.kube.noon.member.domain.Member> findMemberByNickname(String nickname);

    Optional<com.kube.noon.member.domain.Member> findMemberByPhoneNumber(String phoneNumber);

    List<com.kube.noon.member.domain.Member> findMemberListByCriteria(MemberSearchCriteriaDto criteria);

    Optional<MemberRelationship> findMemberRelationship(String fromId, String toId);

    List<MemberRelationship> findMemberRelationshipListByCriteria(MemberRelationshipSearchCriteriaDto criteria);

    void updateMember(com.kube.noon.member.domain.Member member);

    void updatePassword(String memberId, String newPassword);

    void updatePhoneNumber(String memberId, String newPhoneNumber);

    void updateMemberProfilePhoto(String memberId, String newProfilePhotoUrl);

    void updateMemberRelationship(MemberRelationship memberRelationship);

    void deleteMemberRelationship(String fromId, String toId);

    void deleteMember(String memberId);
}

