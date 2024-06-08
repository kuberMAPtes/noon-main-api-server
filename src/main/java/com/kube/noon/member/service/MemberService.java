package com.kube.noon.member.service;

import com.kube.noon.member.domain.Member;
import com.kube.noon.member.domain.MemberRelationship;
import com.kube.noon.member.dto.*;

import java.util.List;
import java.util.Optional;

public interface MemberService {

    void addMember(AddMemberDto memberDto);//JPA

    void addMemberRelationship(AddMemberRelationshipDto addMemberRelationshipDto);

    Optional<Member> findMemberById(String memberId);//JPA

    //레포지토리에서 없음
    Optional<MemberProfileDto> findMemberProfileById(String memberId);

    Optional<Member> findMemberByNickname(String nickname);//JPA

    Optional<Member> findMemberByPhoneNumber(String phoneNumber);

    List<Member> findMemberListByAdmin(MemberSearchCriteriaDto searchDto);//JPA

    List<MemberRelationship> findMemberRelationshipListByAdmin(MemberRelationshipSearchCriteriaDto criteriaDto);

    List<MemberRelationship> findFollowingList(String memberId);

    List<MemberRelationship> findFollowerList(String memberId);

    List<MemberRelationship> findBlockingList(String memberId);

    List<MemberRelationship> findBlockerList(String memberId);

    Optional<MemberRelationship> findMemberRelationship(String fromId, String toId);

    void updateMember(UpdateMemberDto updateMemberDto);

    void updatePassword(String memberId, String newPassword);

    void updatePhoneNumber(String memberId,String newPassword);

    void updateMemberProfilePhoto(String memberId, String newProfilePhotoUrl);

    void updateDajungScore(String memberId, int dajungScore);

    void deleteMemberRelationship(DeleteMemberRelationshipDto dto);

    void deleteMember(String memberId);

    boolean checkNickname(String nickname);

    boolean checkMemberId(String memberId);

    boolean checkPassword(String email, String password);

    boolean checkPhoneNumber(String phoneNumber);

    boolean checkBadWord(String word);
}
