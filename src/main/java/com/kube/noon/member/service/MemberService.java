package com.kube.noon.member.service;

import com.kube.noon.member.domain.Member;
import com.kube.noon.member.dto.member.*;
import com.kube.noon.member.dto.memberRelationship.AddMemberRelationshipDto;
import com.kube.noon.member.dto.memberRelationship.DeleteMemberRelationshipDto;
import com.kube.noon.member.dto.memberRelationship.MemberRelationshipDto;
import com.kube.noon.member.dto.search.MemberRelationshipSearchCriteriaDto;
import com.kube.noon.member.dto.search.MemberSearchCriteriaDto;
import org.springframework.data.domain.Page;

import java.util.Optional;

public interface MemberService {

    void addMember(AddMemberDto memberDto);//JPA

    void addMemberRelationship(AddMemberRelationshipDto addMemberRelationshipDto);

    MemberDto findMemberById(String fromId,String memberId);//JPA

    //다른 서비스가 사용하는 목적으로 존재
    Optional<Member> findMemberById(String memberId);

    MemberProfileDto findMemberProfileById(String fromId, String memberId);

    MemberDto findMemberByNickname(String fromId, String nickname);//JPA

    MemberDto findMemberByNickname(String nickname);

    MemberDto findMemberByPhoneNumber(String phoneNumber);

    Page<MemberDto> findMemberListByCriteria(String fromId, MemberSearchCriteriaDto memberSearchCriteriaDto, int page, int size);//JPA

    Page<MemberRelationshipDto> findMemberRelationshipListByCriteria(String fromId, MemberRelationshipSearchCriteriaDto memberRelationshipSearchCriteriaDto, int page, int size);

    MemberRelationshipDto findMemberRelationship(String fromId, String toId);

    void updateMember(UpdateMemberDto updateMemberDto);

    void updatePassword(UpdatePasswordDto updatePasswordDto);

    void updatePhoneNumber(UpdatePhoneNumberDto updatePhoneNumberDto);

    void updateMemberProfilePhotoUrl(UpdateMemberProfilePhotoUrlDto updateMemberProfilePhotoUrlDto);

    void updateMemberProfileIntro(UpdateMemberProfileIntroDto updateMemberProfileIntroDto);

    void updateDajungScore(UpdateMemberDajungScoreDto updateMemberDajungScoreDto);

    void deleteMemberRelationship(DeleteMemberRelationshipDto dto);

    void deleteMember(String memberId);

    void checkNickname(String nickname);

    void checkMemberId(String memberId);

    void checkMemberIdExisted(String memberId);

    void checkPhoneNumberAndMemberId(String phoneNumber, String memberId);

    void checkLoginMemberIdPattern(String memberId);

    void checkPassword(String memberId, String password);

    void checkPhoneNumber(String phoneNumber);

    void checkBadWord(String word);

}
