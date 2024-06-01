package com.kube.noon.member.repository;

import com.kube.noon.member.domain.Member;
import com.kube.noon.member.domain.MemberRelationship;
import com.kube.noon.member.domain.Search;
import com.kube.noon.member.dto.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class MemberRepositoryImpl implements MemberRepository {
    private final MemberJpaRepository jpaRepository;
    private final MemberMapper mapper;

    @Override
    public void addMember(Member member) {

    }

    @Override
    public void addMemberRelationship(MemberRelationship memberRelationship) {

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
    public List<Member> findMemberList(Search search) {
        return List.of();
    }

    @Override
    public List<MemberRelationship> findMemberRelationshipList(Search search) {
        return List.of();
    }

    @Override
    public void updateMember(Member member) {

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
