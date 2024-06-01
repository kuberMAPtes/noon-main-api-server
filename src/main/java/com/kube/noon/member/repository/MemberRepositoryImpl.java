package com.kube.noon.member.repository;

import com.kube.noon.member.domain.Member;
import com.kube.noon.member.domain.MemberRelationship;
import com.kube.noon.member.domain.Search;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

import static com.kube.noon.member.enums.MemberSearchCondition.*;


@Repository
@RequiredArgsConstructor
public class MemberRepositoryImpl implements MemberRepository {
    private final MemberJpaRepository memberJpaRepository;
    private final MemberRelationshipJpaRepository relationshipJpaRepository;
    private final MemberMapper mapper;

    @Override
    public void addMember(Member member) {
        memberJpaRepository.save(member);
    }

    @Override
    public void addMemberRelationship(MemberRelationship memberRelationship) {
        relationshipJpaRepository.save(memberRelationship);
    }

    @Override
    public Optional<Member> findMemberById(String memberId) {
        return memberJpaRepository.findMemberById(memberId);
    }

    @Override
    public Optional<Member> findMemberByNickname(String nickname) {
        return memberJpaRepository.findMemberByNickname(nickname);
    }

    @Override
    public List<Member> findMemberList(Search<?> search) {

        return switch (search.getMemberSearchCondition()) {
            case MEMBER_ID ->
                    memberJpaRepository.findMemberByMemberIdContainingIgnoreCase((String) search.getSearchKeyword());
            case NICKNAME ->
                    memberJpaRepository.findMemberByNicknameContainingIgnoreCase((String) search.getSearchKeyword());
            case UNLOCK_TIME ->
                    memberJpaRepository.findMemberByUnlockTimeBetween(search.getStartTime(), search.getEndTime());
            case PHONE_NUMBER ->
                    memberJpaRepository.findMemberByPhoneNumberContainingIgnoreCase((String) search.getSearchKeyword());
            case SIGNED_OFF -> memberJpaRepository.findMemberBySignOff((boolean) search.getSearchKeyword());
        };

    }

    //팔로잉리스트 팔로워리스트 차단리스트
    @Override
    public List<MemberRelationship> findMemberRelationshipList(Search<?> search) {
        return switch(search.getMemberRelationshipSearchCondition()){
            case FOLLOW ->
                memberJpaRepository.findMemberRelationshipBy
        }
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
