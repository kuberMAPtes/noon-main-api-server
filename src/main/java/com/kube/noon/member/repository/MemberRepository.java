package com.kube.noon.member.repository;

import com.kube.noon.member.domain.Member;
import com.kube.noon.member.domain.MemberRelationship;
import com.kube.noon.member.dto.MemberRelationshipSearchCriteriaDto;
import com.kube.noon.member.dto.MemberSearchCriteriaDto;
import com.kube.noon.member.enums.RelationshipType;

import java.util.List;
import java.util.Optional;

/**
 * 서비스레이어만들때 주의할 점
 * 다정온도max,제한
 * findMemberRelationshipListByCriteria는 멤버도 리턴해야돼
 */
public interface MemberRepository {

    void addMember(Member member);

    void addMemberRelationship(MemberRelationship memberRelationship);

    Optional<Member> findMemberById(String memberId);

    Optional<Member> findMemberByNickname(String nickname);

    List<Member> findMemberListByCriteria(MemberSearchCriteriaDto criteria);

    List<MemberRelationship> findMemberRelationshipListByCriteria(MemberRelationshipSearchCriteriaDto criteria);

    void updateMember(Member member);

    void updatePassword(String memberId, String newPassword);

    void updatePhoneNumber(String memberId, String newPhoneNumber);

    void updateMemberProfilePhoto(String memberId, String newProfilePhotoUrl);

    void deleteMemberRelationship(String toId, String fromId, RelationshipType relationshipType);

    void deleteMember(String memberId);
}

