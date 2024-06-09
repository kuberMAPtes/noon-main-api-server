package com.kube.noon.member.validator;

import com.kube.noon.common.validator.IllegalServiceCallException;
import com.kube.noon.common.validator.Validator;
import com.kube.noon.member.dto.*;
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
    public void findMemberByPhoneNumberByAdmin(String fromId, String phoneNumber) {
        memberScanner.imoDataNotNull(fromId);
        memberScanner.imoMemberIsAdmin(fromId);
        memberScanner.imoDataNotNull(phoneNumber);
        memberScanner.imoMemberIdExist(fromId);
    }
    public void findMemberListByCriteria(String fromId, MemberSearchCriteriaDto searchDto, int page, int size) {
        memberScanner.imoDataNotNull(fromId);
        memberScanner.imoDataNotNull(searchDto);

    }
    public void findMemberRelationship(String fromId, String toId) {
        memberScanner.imoDataNotNull(fromId);
        memberScanner.imoDataNotNull(toId);
    }
    public void findMemberRelationshipListByCriteria(String fromId, MemberRelationshipSearchCriteriaDto searchDto,int page,int size) {
        memberScanner.imoDataNotNull(fromId);
        memberScanner.imoDataNotNull(searchDto);
    }
    public void updateMember(UpdateMemberDto updateMemberDto) {
        memberScanner.imoDtoFieldO(updateMemberDto);
    }
    public void updatePassword(String memberId, String newPassword) {
        memberRepository.findMemberById(memberId).orElseThrow(() -> new IllegalServiceCallException("존재하지 않는 회원입니다."));
        memberScanner.imoDataNotNull(memberId);
        memberScanner.imoDataNotNull(newPassword);
    }

    public void updatePhoneNumber(String memberId, String newPhoneNumber) {
        memberRepository.findMemberById(memberId).orElseThrow(() -> new IllegalServiceCallException("존재하지 않는 회원입니다."));
        memberScanner.imoDataNotNull(memberId);
        memberScanner.imoDataNotNull(newPhoneNumber);
        memberScanner.imoPhoneNumberPatternO(newPhoneNumber);
    }

    public void updateMemberProfilePhoto(String memberId, String newProfilePhotoUrl) {
        memberRepository.findMemberById(memberId).orElseThrow(() -> new IllegalServiceCallException("존재하지 않는 회원입니다."));
        memberScanner.imoDataNotNull(memberId);
        //프로필사진Url은 null이 가능하다.
        memberScanner.imoProfilePhotoUrlPatternO(newProfilePhotoUrl);

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