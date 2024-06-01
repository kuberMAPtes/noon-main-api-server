package com.kube.noon.member.repository;

import com.kube.noon.member.domain.Member;
import com.kube.noon.member.domain.MemberRelationship;
import com.kube.noon.member.domain.Search;
import com.kube.noon.member.dto.*;

import java.util.List;

public interface MemberRepository {

    public void addMember(Member member);//JPA

    public void addMemberRelationship(MemberRelationship memberRelationship
    );

    public Member findMemberById(String memberId);//JPA

    public MemberProfileDto findMemberProfileById(String memberId);

    public Member findMemberByNickname(String nickname);//JPA

    public List<Member> findMemberList(Search search);//JPA

    public List<MemberRelationship> findMemberRelationshipList(Search search);//JPA

    public void updateMember(Member member);

    public void updatePassword(String memberId, String newPassword);

    public void updatePhoneNumber(String memberId,String newPassword);

    public void updateMemberProfilePhoto(String memberId, String newProfilePhotoUrl);

    public void deleteMemberRelationship(String memberRelationshipId);

    public void deleteMember(String memberId);
}