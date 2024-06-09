package com.kube.noon.member.service;

import com.kube.noon.member.domain.Member;
import com.kube.noon.member.dto.*;
import org.springframework.data.domain.Page;

import java.util.Optional;

public interface MemberService {

    void addMember(AddMemberDto memberDto);//JPA

    void addMemberRelationship(AddMemberRelationshipDto addMemberRelationshipDto);

    MemberDto findMemberById(String fromId,String memberId);//JPA

    //다른 서비스가 사용하는 목적으로 존재
    Optional<Member> findMemberById(String memberId);

    MemberProfileDto findMemberProfileById(String fromId,String memberId);

    MemberDto findMemberByNickname(String fromId,String nickname);//JPA

    MemberDto findMemberByPhoneNumberByAdmin(String fromId, String phoneNumber);

    Page<MemberDto> findMemberListByCriteria(String fromId,MemberSearchCriteriaDto memberSearchCriteriaDto, int page, int size);//JPA

    Page<MemberRelationshipDto> findMemberRelationshipListByCriteria(String fromId,MemberRelationshipSearchCriteriaDto memberRelationshipSearchCriteriaDto, int page, int size);

    MemberRelationshipDto findMemberRelationship(String fromId, String toId);

    void updateMember(UpdateMemberDto updateMemberDto);

    void updatePassword(UpdatePasswordDto updatePasswordDto);

    void updatePhoneNumber(UpdatePhoneNumberDto updatePhoneNumberDto);

    void updateMemberProfilePhoto(UpdateMemberProfilePhotoUrlDto updateMemberProfilePhotoUrlDto);

    void updateDajungScore(String memberId, int dajungScore);

    void deleteMemberRelationship(DeleteMemberRelationshipDto dto);

    void deleteMember(String memberId);

    boolean checkNickname(String nickname);

    boolean checkMemberId(String memberId);

    boolean checkPassword(String email, String password);

    boolean checkPhoneNumber(String phoneNumber);

    boolean checkBadWord(String word);
}
