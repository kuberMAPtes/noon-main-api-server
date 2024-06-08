package com.kube.noon.member.service;

import com.kube.noon.member.domain.Member;
import com.kube.noon.member.domain.MemberRelationship;
import com.kube.noon.member.dto.*;
import org.springframework.data.domain.Page;

import java.util.Optional;

public interface MemberService {

    void addMember(AddMemberDto memberDto);//JPA

    void addMemberRelationship(AddMemberRelationshipDto addMemberRelationshipDto);

    Optional<Member> findMemberById(String memberId);//JPA

    //레포지토리에서 없음
    Optional<MemberProfileDto> findMemberProfileById(String memberId);

    Optional<Member> findMemberByNickname(String nickname);//JPA

    Optional<Member> findMemberByPhoneNumber(String phoneNumber);

    Page<Member> findMemberListByAdmin(MemberSearchCriteriaDto searchDto, int page, int size);//JPA

    Page<MemberRelationship> findMemberRelationshipListByCriteria(MemberRelationshipSearchCriteriaDto criteriaDto, int page, int size);

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
