package com.kube.noon.member.service;

import com.kube.noon.member.domain.Member;
import com.kube.noon.member.domain.MemberRelationship;
import com.kube.noon.member.dto.*;

import java.util.List;
import java.util.Optional;

public interface MemberService {

    void addMember(AddMemberDto memberDto);//JPA

    void addMemberRelationship(MemberRelationshipDto memberRelationshipDto);

    Optional<Member> findMemberById(String memberId);//JPA

    //레포지토리에서 없음
    Optional<MemberProfileDto> findMemberProfileById(String memberId);

    Optional<Member> findMemberByNickname(String nickname);//JPA

    List<Member> findMemberListByCriteria(MemberSearchCriteriaDto searchDto);//JPA

    List<MemberRelationship> findMemberRelationshipListByCriteria(MemberRelationshipSearchCriteriaDto criteriaDto);

    Optional<MemberRelationship> findMemberRelationship(String fromId, String toId);

    void updateMember(UpdateMemberDto updateMemberDto);

    void updatePassword(String memberId, String newPassword);

    void updatePhoneNumber(String memberId,String newPassword);

    void updateMemberProfilePhoto(String memberId, String newProfilePhotoUrl);

    void deleteMemberRelationship(MemberRelationshipDto dto);

    void deleteMember(String memberId);

    boolean checkNickname(String nickname);

    boolean checkMemberId(String memberId);

    boolean checkPassword(String email, String password);

    boolean checkBadWord(String word);
}
