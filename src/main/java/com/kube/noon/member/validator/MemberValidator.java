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
    public void findMemberById(String memberId) {
        memberScanner.imoDataNotNull(memberId);
    }
    public void findMemberProfileById(String memberId) {
        memberScanner.imoDataNotNull(memberId);
    }
    public void findMemberByNickname(String nickname) {
        memberScanner.imoDataNotNull(nickname);
    }
    public void findMemberByPhoneNumber(String phoneNumber) {
        memberScanner.imoDataNotNull(phoneNumber);
    }
    public void findMemberListByCriiteria(MemberSearchCriteriaDto searchDto) {
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
        memberScanner.imoDataNotNull(newProfilePhotoUrl);
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

    }

    public void checkNickname(String nickname) {
        memberScanner.imoDataNotNull(nickname);
        memberScanner.imoNicknamePatternO(nickname);
    }

    public void checkMemberId(String memberId) {
        memberScanner.imoDataNotNull(memberId);
    }

    public void checkPassword(String email, String password) {
        memberScanner.imoDataNotNull(email);
        memberScanner.imoDataNotNull(password);
        memberScanner.imoPasswordPatternO(password);
    }

    public void checkPhoneNumber(String phoneNumber) {
        memberScanner.imoDataNotNull(phoneNumber);
        memberScanner.imoPhoneNumberPatternO(phoneNumber);
    }

    public void checkBadWord(String word) {
        memberScanner.imoDataNotNull(word);
    }
}