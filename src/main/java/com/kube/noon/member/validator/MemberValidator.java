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
        memberScanner.scanIsDataNull(dto);
        memberScanner.scanIsMemberSignedOff(dto);
        memberScanner.scanDtoField(dto);
    }
    public void addMemberRelationship(AddMemberRelationshipDto dto) {
        memberScanner.scanIsDataNull(dto);
        memberScanner.scanIsMemberSignedOff(dto);
        memberScanner.scanDtoField(dto);
    }
    public void findMemberById(String memberId) {
        memberScanner.scanIsDataNull(memberId);
    }
    public void findMemberProfileById(String memberId) {
        memberScanner.scanIsDataNull(memberId);
    }
    public void findMemberByNickname(String nickname) {
        memberScanner.scanIsDataNull(nickname);
    }
    public void findMemberByPhoneNumber(String phoneNumber) {
        memberScanner.scanIsDataNull(phoneNumber);
    }
    public void findMemberListByCriiteria(MemberSearchCriteriaDto searchDto) {
        memberScanner.scanIsDataNull(searchDto);

    }
    public void updateMember(UpdateMemberDto updateMemberDto) {
        memberScanner.scanDtoField(updateMemberDto);
    }
    public void updatePassword(String memberId, String newPassword) {
        memberRepository.findMemberById(memberId).orElseThrow(() -> new IllegalServiceCallException("존재하지 않는 회원입니다."));
        memberScanner.scanIsDataNull(memberId);
        memberScanner.scanIsDataNull(newPassword);
    }

    public void updatePhoneNumber(String memberId, String newPhoneNumber) {
        memberRepository.findMemberById(memberId).orElseThrow(() -> new IllegalServiceCallException("존재하지 않는 회원입니다."));
        memberScanner.scanIsDataNull(memberId);
        memberScanner.scanIsDataNull(newPhoneNumber);
        memberScanner.scanPhoneNumberPattern(newPhoneNumber);
    }

    public void updateMemberProfilePhoto(String memberId, String newProfilePhotoUrl) {
        memberRepository.findMemberById(memberId).orElseThrow(() -> new IllegalServiceCallException("존재하지 않는 회원입니다."));
        memberScanner.scanIsDataNull(memberId);
        memberScanner.scanIsDataNull(newProfilePhotoUrl);
        memberScanner.scanProfilePhotoUrlPattern(newProfilePhotoUrl);

    }

    public void updateDajungScore(String memberId, int dajungScore) {
        memberRepository.findMemberById(memberId).orElseThrow(() -> new IllegalServiceCallException("존재하지 않는 회원입니다."));
        memberScanner.scanIsDataNull(memberId);
        memberScanner.scanIsDataNull(dajungScore);
        memberScanner.scanDajungScorePattern(dajungScore);
    }

    public void deleteMemberRelationship(DeleteMemberRelationshipDto dto) {
        memberScanner.scanIsDataNull(dto);
        memberScanner.scanDtoField(dto);
    }

    public void deleteMember(String memberId) {
        memberScanner.scanIsDataNull(memberId);
        memberScanner.scanIsMemberSignedOff(memberId);

    }

    public void checkNickname(String nickname) {
        memberScanner.scanIsDataNull(nickname);
        memberScanner.scanNicknamePattern(nickname);
    }

    public void checkMemberId(String memberId) {
        memberScanner.scanIsDataNull(memberId);
    }

    public void checkPassword(String email, String password) {
        memberScanner.scanIsDataNull(email);
        memberScanner.scanIsDataNull(password);
        memberScanner.scanPasswordPattern(password);
    }

    public void checkPhoneNumber(String phoneNumber) {
        memberScanner.scanIsDataNull(phoneNumber);
        memberScanner.scanPhoneNumberPattern(phoneNumber);
    }

    public void checkBadWord(String word) {
        memberScanner.scanIsDataNull(word);
    }
}