package com.kube.noon.member.validator;

import com.kube.noon.common.validator.IllegalServiceCallException;
import com.kube.noon.common.validator.Validator;
import com.kube.noon.member.dto.member.*;
import com.kube.noon.member.dto.memberRelationship.AddMemberRelationshipDto;
import com.kube.noon.member.dto.memberRelationship.DeleteMemberRelationshipDto;
import com.kube.noon.member.dto.search.MemberRelationshipSearchCriteriaDto;
import com.kube.noon.member.dto.search.MemberSearchCriteriaDto;
import com.kube.noon.member.repository.MemberRepository;
import com.kube.noon.member.service.impl.MemberServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

@Validator(targetClass = MemberServiceImpl.class)
@Slf4j
public class MemberValidator {


    private final MemberRepository memberRepository;
    private final MemberScanner memberScanner;

    @Autowired
    public MemberValidator(MemberRepository memberRepository,MemberScanner memberScanner) {
        this.memberRepository = memberRepository;
        this.memberScanner = memberScanner;
    }

    public void addMember(AddMemberDto dto) {
        memberScanner.imoDataNotNull(dto);
        memberScanner.imoDtoFieldO(dto);
    }
    public void addMemberRelationship(AddMemberRelationshipDto dto) {
        memberScanner.imoDataNotNull(dto);
        memberScanner.imoMemberNotSignedOff(dto);
        memberScanner.imoDtoFieldO(dto);
    }
    public void findMemberById(String fromId,String memberId){
        memberScanner.imoDataNotNull(fromId);
        memberScanner.imoMemberNotSignedOff(fromId);
        memberScanner.imoDataNotNull(memberId);
    }
    public void findMemberById(String memberId) {
        memberScanner.imoDataNotNull(memberId);
        memberScanner.imoMemberIdExist(memberId);

    }
    public void findMemberProfileById(String fromId, String memberId) {
        memberScanner.imoDataNotNull(fromId);
        memberScanner.imoDataNotNull(memberId);
        memberScanner.imoMemberIdExist(fromId);
        memberScanner.imoMemberIdExist(memberId);
    }
    public void findMemberByNickname(String fromId, String nickname) {
        memberScanner.imoDataNotNull(fromId);
        memberScanner.imoDataNotNull(nickname);
        memberScanner.imoMemberIdExist(fromId);
        memberScanner.imoMemberNicknameExist(nickname);
    }
    public void findMemberByPhoneNumber(String phoneNumber) {
        memberScanner.imoDataNotNull(phoneNumber);
        memberScanner.imoMemberPhoneNumberExist(phoneNumber);
    }
    public void findMemberListByCriteria(String fromId, MemberSearchCriteriaDto dto, int page, int size) {
        memberScanner.imoDataNotNull(fromId);
        memberScanner.imoDataNotNull(dto);

    }
    public void findMemberRelationship(String fromId, String toId) {
        memberScanner.imoDataNotNull(fromId);
        memberScanner.imoDataNotNull(toId);
    }
    public void findMemberRelationshipListByCriteria(String fromId, MemberRelationshipSearchCriteriaDto dto, int page, int size) {
        memberScanner.imoDataNotNull(fromId);
        memberScanner.imoDataNotNull(dto);
    }
    public void updateMember(UpdateMemberDto dto) {
        memberScanner.imoDataNotNull(dto);
        memberScanner.imoDtoFieldO(dto);
    }
    public void updatePassword(UpdatePasswordDto dto) {
        memberScanner.imoDataNotNull(dto);
        memberScanner.imoDtoFieldO(dto);
    }

    public void updatePhoneNumber(UpdatePhoneNumberDto dto) {
        memberScanner.imoDataNotNull(dto);
        memberScanner.imoDtoFieldO(dto);
    }

    public void updateMemberProfilePhotoUrl(UpdateMemberProfilePhotoUrlDto dto) {
        memberScanner.imoDataNotNull(dto);
        memberScanner.imoDtoFieldO(dto);
    }
    public void updateMemberProfileIntro(UpdateMemberProfileIntroDto dto) {
        memberScanner.imoDataNotNull(dto);
        memberScanner.imoDtoFieldO(dto);
    }

    public void updateMemberDajunScore(UpdateMemberDajungScoreDto dto) {
        memberScanner.imoDataNotNull(dto);
        memberScanner.imoDtoFieldO(dto);
    }



    public void updateDajungScore(String memberId, int dajungScore) {
        memberRepository.findMemberById(memberId).orElseThrow(() -> new IllegalServiceCallException("존재하지 않는 회원입니다."));
        memberScanner.imoDataNotNull(memberId);
        memberScanner.imoDataNotNull(dajungScore);
        memberScanner.imoDajungScorePatternO(dajungScore);
    }

    public void deleteMemberRelationship(DeleteMemberRelationshipDto dto) {
        memberScanner.imoDataNotNull(dto);
        memberScanner.imoDtoFieldO(dto);
    }

    public void deleteMember(String memberId) {
        memberScanner.imoDataNotNull(memberId);
        memberScanner.imoMemberNotSignedOff(memberId);
        memberScanner.imoMemberIdExist(memberId);
    }

    public void checkNickname(String nickname) {
        memberScanner.imoDataNotNull(nickname);
        memberScanner.imoNicknamePatternO(nickname);
        memberScanner.imoMemberNicknameNotExist(nickname);
    }

    public void checkMemberId(String memberId) {
        memberScanner.imoDataNotNull(memberId);
        memberScanner.imoMemberIdPatternO(memberId);
        memberScanner.imoMemberIdNotExist(memberId);
    }

    public void checkPassword(String memberId, String password) {
        memberScanner.imoDataNotNull(memberId);
        memberScanner.imoDataNotNull(password);
        memberScanner.imoPwdPatternO(password);
    }

    public void checkPhoneNumber(String phoneNumber) {
        memberScanner.imoDataNotNull(phoneNumber);
        memberScanner.imoPhoneNumberPatternO(phoneNumber);
        memberScanner.imoMemberPhoneNumberNotExist(phoneNumber);
    }

    public void checkBadWord(String word) {
        memberScanner.imoDataNotNull(word);
    }
}