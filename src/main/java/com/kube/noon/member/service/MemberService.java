package com.kube.noon.member.service;

import com.kube.noon.member.domain.Member;
import com.kube.noon.member.domain.MemberRelationship;
import com.kube.noon.member.dto.*;

import java.util.List;

public interface MemberService {

    public void addMember(AddMemberDto memberDto);//JPA

    public void addMemberRelationship(AddMemberRelationshipDto addMemberRelationshipDto);

    public Member findMemberById(String memberId);//JPA

    public MemberProfileDto findMemberProfileById(String memberId);

    public Member findMemberByNickname(String nickname);//JPA

    public List<Member> findMemberList(MemberSearchCriteriaDto searchDto);//JPA

    public List<MemberRelationship> findMemberRelationshipList(MemberRelationshipSearchCriteriaDto criteriaDto);

    public void updateMember(UpdateMemberDto updateMemberDto);

    public void updatePassword(String memberId, String newPassword);

    public void updatePhoneNumber(String memberId,String newPassword);

    public void updateMemberProfilePhoto(String memberId, String newProfilePhotoUrl);

    public void deleteMemberRelationship(String memberRelationshipId);

    public void deleteMember(String memberId);


}
