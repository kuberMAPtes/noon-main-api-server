package com.kube.noon.member.service.impl;

import com.kube.noon.member.binder.MemberBinder;
import com.kube.noon.member.domain.Member;
import com.kube.noon.member.domain.MemberRelationship;
import com.kube.noon.member.dto.*;
import com.kube.noon.member.repository.MemberRepository;
import com.kube.noon.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MeberServiceImpl implements MemberService {

    private final MemberRepository memberRepository;


    @Override
    public void addMember(AddMemberDto memberDto) {
        MemberBinder.INSTANCE.toMember(memberDto);
    }

    @Override
    public void addMemberRelationship(AddMemberRelationshipDto addMemberRelationshipDto) {

    }

    @Override
    public Member findMemberById(String memberId) {
        return null;
    }

    @Override
    public MemberProfileDto findMemberProfileById(String memberId) {
        return null;
    }

    @Override
    public Member findMemberByNickname(String nickname) {
        return null;
    }

    @Override
    public List<Member> findMemberListByCriteria(MemberSearchCriteriaDto searchDto) {
        return List.of();
    }

    @Override
    public List<MemberRelationship> findMemberRelationshipListByCriteria(MemberRelationshipSearchCriteriaDto criteriaDto) {
        return List.of();
    }


    @Override
    public void updateMember(UpdateMemberDto updateMemberDto) {

    }

    @Override
    public void updatePassword(String memberId, String newPassword) {

    }

    @Override
    public void updatePhoneNumber(String memberId, String newPassword) {

    }

    @Override
    public void updateMemberProfilePhoto(String memberId, String newProfilePhotoUrl) {

    }

    @Override
    public void deleteMemberRelationship(String memberRelationshipId) {

    }

    @Override
    public void deleteMember(String memberId) {

    }
}
