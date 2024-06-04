package com.kube.noon.member.service;

import com.kube.noon.member.domain.Member;
import com.kube.noon.member.domain.MemberRelationship;
import com.kube.noon.member.dto.*;

import java.util.List;
import java.util.Optional;

public interface MemberService {

    public void addMember(AddMemberDto memberDto);//JPA

    public void addMemberRelationship(MemberRelationshipDto memberRelationshipDto);

    public Optional<Member> findMemberById(String memberId);//JPA

    //레포지토리에서 없음
    public Optional<MemberProfileDto> findMemberProfileById(String memberId);

    public Optional<Member> findMemberByNickname(String nickname);//JPA

    public List<Member> findMemberListByCriteria(MemberSearchCriteriaDto searchDto);//JPA

    public List<MemberRelationship> findMemberRelationshipListByCriteria(MemberRelationshipSearchCriteriaDto criteriaDto);

    public Optional<MemberRelationship> findMemberRelationship(String fromId, String toId);

    public void updateMember(UpdateMemberDto updateMemberDto);

    public void updatePassword(String memberId, String newPassword);

    public void updatePhoneNumber(String memberId,String newPassword);

    public void updateMemberProfilePhoto(String memberId, String newProfilePhotoUrl);

    public void deleteMemberRelationship(MemberRelationshipDto dto);

    public void deleteMember(String memberId);

}
