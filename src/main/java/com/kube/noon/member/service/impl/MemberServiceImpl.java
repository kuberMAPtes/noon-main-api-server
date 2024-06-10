package com.kube.noon.member.service.impl;

import com.kube.noon.common.PublicRange;
import com.kube.noon.common.badwordfiltering.BadWordFilterAgent;
import com.kube.noon.common.binder.DtoEntityBinder;
import com.kube.noon.feed.service.FeedService;
import com.kube.noon.member.domain.Member;
import com.kube.noon.member.domain.MemberRelationship;
import com.kube.noon.member.dto.member.*;
import com.kube.noon.member.dto.memberRelationship.AddMemberRelationshipDto;
import com.kube.noon.member.dto.memberRelationship.DeleteMemberRelationshipDto;
import com.kube.noon.member.dto.memberRelationship.MemberRelationshipDto;
import com.kube.noon.member.dto.search.MemberRelationshipSearchCriteriaDto;
import com.kube.noon.member.dto.search.MemberSearchCriteriaDto;
import com.kube.noon.member.dto.util.RandomData;
import com.kube.noon.member.enums.RelationshipType;
import com.kube.noon.member.enums.Role;
import com.kube.noon.member.exception.*;
import com.kube.noon.member.repository.MemberRepository;
import com.kube.noon.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 *
 */
@Validated
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class MemberServiceImpl implements MemberService {

    private final MemberRepository memberRepository;
    private final FeedService feedService;
    private final BadWordFilterAgent badWordFilterAgent;

//    private final SettingService settingService;

    @Override
    public void addMember(AddMemberDto dto) {
        try {
            log.info("회원 추가 중 : DTO {}", dto);
            Member member = DtoEntityBinder.INSTANCE.toEntity(dto);
            log.info("서비스에서 member 검증: {}", member);

            // 탈퇴된 회원이 다시 회원가입하면 탈퇴상태를 변경
            memberRepository.findMemberById(member.getMemberId()).ifPresent(memb -> {
                if (memb.getSignedOff()) {
                    memberRepository.updateMember(member);
                }
            });

            if (Boolean.TRUE.equals(dto.getSocialSignUp())) {
                dto.setPwd("social_sign_up");
            }
            memberRepository.addMember(member);
            log.info("회원 추가 성공 : DTO {}", dto);
        } catch (MemberCreationException e) {
            log.error("회원 추가 중 오류 발생: {}", dto, e);
            throw e;
        }
    }

    @Override
    public void addMemberRelationship(AddMemberRelationshipDto dto) {
        try {
            log.info("회원 관계 추가 중 : DTO {}", dto);
            dto.setActivated(true);

            MemberRelationship memberRelationship = DtoEntityBinder.INSTANCE.toEntity(dto);

            memberRepository.findMemberRelationship(dto.getFromId(), dto.getToId())
                    .ifPresentOrElse(
                            mr -> {
                                log.info("업데이트 합니다");
                                memberRepository.updateMemberRelationship(memberRelationship);
                            },
                            () -> {
                                log.info("관계를 추가합니다.");
                                memberRepository.addMemberRelationship(memberRelationship);
                            }
                    );

            log.info("회원 관계 추가 성공 : DTO {}", memberRelationship);
        } catch (MemberRelationshipCreationException e) {
            log.error("회원 관계 추가 중 오류 발생: {}", dto, e);
            throw e;
        }
    }

    @Override
    public MemberDto findMemberById(String fromId, String memberId) {
        try {
            log.info("회원 찾는 중 ID: {}", memberId);

            return memberRepository.findMemberById(fromId)
                    .filter(fromMember -> fromMember.getMemberRole().equals(Role.ADMIN) || fromId.equals(memberId))
                    .flatMap(fromMember -> memberRepository.findMemberById(memberId)
                            .map(member -> {
                                MemberDto memberDto = DtoEntityBinder.INSTANCE.toDto(member, MemberDto.class);
                                return memberDto;
                            }))
                    .orElse(null); // 조회된 회원이 없으면 null 반환

        } catch (MemberNotFoundException e) {
            log.error("회원 조회 중 오류 발생 {} {}", fromId,memberId , e);
            throw e;
        }
    }

    @Override
    public Optional<Member> findMemberById(String memberId) {
        try {
            return memberRepository.findMemberById(memberId);
        } catch (MemberNotFoundException e) {
            log.error("회원 조회 중 오류 발생: ID={}", memberId, e);
            throw e;
        }
    }

    @Override
    public MemberProfileDto findMemberProfileById(String fromId, String memberId) {
        try {
            log.info("회원 프로필 찾는 중 ID: {}", memberId);

            // fromId로 조회한 회원 정보를 기반으로 프로필 조회를 처리
            return memberRepository.findMemberById(fromId)
                    .flatMap(fromMember -> handleProfileRetrieval(fromMember, fromId, memberId))
                    .orElse(null);  // 조건을 만족하지 않으면 null 반환

        } catch (MemberNotFoundException e) {
            log.error("회원 프로필 조회 중 오류 발생: ID={}", memberId, e);
            throw e;
        }
    }

    private Optional<MemberProfileDto> handleProfileRetrieval(Member fromMember, String fromId, String memberId) {
        if (fromMember.getMemberRole().equals(Role.ADMIN)) {
            // 관리자이면 모든 회원의 프로필 조회 가능
            return memberRepository.findMemberById(memberId)
                    .map(findedMember -> createMemberProfileDto(findedMember, memberId));
        } else if (fromId.equals(memberId)) {
            // 자기 자신을 조회하는 경우
            return memberRepository.findMemberById(memberId)
                    .map(findedMember -> createMemberProfileDto(findedMember, memberId));
        } else {
            // 다른 사람의 프로필을 조회하는 경우
            return findOtherMemberProfile(fromId, memberId);
        }
    }

    private Optional<MemberProfileDto> findOtherMemberProfile(String fromId, String memberId) {
        // 차단 여부 확인
        if (fromMemberIsBlocked(memberId, fromId)) {
            return Optional.empty();  // 차단된 경우 빈 Optional 반환
        }

        // 회원 관계 확인
        MemberRelationshipDto memberRelationshipDto = findMemberRelationship(fromId, memberId);
        return memberRepository.findMemberById(memberId)
                .filter(findedMember -> {
                    PublicRange profilePublicRange = findedMember.getMemberProfilePublicRange();
                    // 프로필 공개 범위에 따른 접근 권한 확인
                    switch (profilePublicRange) {
                        case PUBLIC:
                            return true;  // 공개된 프로필은 누구나 접근 가능
                        case PRIVATE:
                            return false;  // 비공개 프로필은 접근 불가
                        case FOLLOWER_ONLY:
                            return memberRelationshipDto.getRelationshipType() == RelationshipType.FOLLOW;  // 팔로우 관계일 때 접근 가능
                        case MUTUAL_ONLY:
                            return isMutualFollow(fromId, memberId);  // 상호 팔로우 관계일 때 접근 가능
                        default:
                            return false;  // 기타 경우 접근 불가
                    }
                })
                .map(findedMember -> createMemberProfileDto(findedMember, memberId));
    }

    private boolean isMutualFollow(String fromId, String memberId) {
        // fromId가 memberId를 팔로우하고, memberId가 fromId를 팔로우하는지 확인
        MemberRelationshipDto relationship1 = findMemberRelationship(fromId, memberId);
        MemberRelationshipDto relationship2 = findMemberRelationship(memberId, fromId);
        return relationship1.getRelationshipType() == RelationshipType.FOLLOW && relationship2.getRelationshipType() == RelationshipType.FOLLOW;
    }

    //MemberProfileDto 객체 생성
    private MemberProfileDto createMemberProfileDto(Member findedMember, String memberId) {
        // Member 객체를 MemberProfileDto 객체로 변환
        MemberProfileDto memberProfileDto = DtoEntityBinder.INSTANCE.toDto(findedMember, MemberProfileDto.class);
        // 회원의 피드 목록 설정
        memberProfileDto.setFeedDtoList(feedService.getFeedListByMember(memberId));
        return memberProfileDto;
    }

    @Override
    public MemberDto findMemberByNickname(String fromId, String nickname) {
        try {
            log.info("회원 찾는 중 닉네임: {}", nickname);

            return memberRepository.findMemberById(fromId)
                    .flatMap(fromMember -> handleMemberSearchByNickname(fromMember, nickname, fromId))
                    .orElseGet(() -> {
                        log.info("회원이 없습니다");
                        return null;
                    });

        } catch (MemberNotFoundException e) {
            log.error("회원 조회 중 오류 발생: fromId={} 닉네임={}",fromId, nickname, e);
            throw e;
        }
    }

    private Optional<MemberDto> handleMemberSearchByNickname(Member fromMember, String nickname, String fromId) {
        if (fromMember.getMemberRole().equals(Role.ADMIN)) {
            return memberRepository.findMemberByNickname(nickname)
                    .map(member -> DtoEntityBinder.INSTANCE.toDto(member, MemberDto.class));
        } else {
            return memberRepository.findMemberByNickname(nickname)
                    .filter(member -> !fromMemberIsBlocked(member.getMemberId(), fromId))
                    .map(member -> DtoEntityBinder.INSTANCE.toDto(member, MemberDto.class));
        }
    }

    private boolean fromMemberIsBlocked(String memberId, String fromId) {
        MemberRelationshipDto blockRelationshipDto = findMemberRelationship(memberId, fromId);
        return blockRelationshipDto.getRelationshipType() == RelationshipType.BLOCK;
    }

    @Override
    public MemberDto findMemberByPhoneNumber(String phoneNumber) {
        try {
            log.info("회원 찾는 중 전화번호: {}", phoneNumber);

            Member member = memberRepository.findMemberByPhoneNumber(phoneNumber).orElseGet(() -> {
                log.info("회원이 없습니다");
                return null;
            });

            return DtoEntityBinder.INSTANCE.toDto(member, MemberDto.class);
        } catch (MemberNotFoundException e) {
            log.error("회원 조회 중 오류 발생:전화번호={}",phoneNumber, e);
            throw e;
        }
    }

    @Override
    public Page<MemberDto> findMemberListByCriteria(String fromId, MemberSearchCriteriaDto criteriaDto, int page, int size) {
        try {
            log.info("회원 리스트 찾는 중 : {}", criteriaDto);

            Page<Member> memberPage = memberRepository.findMemberListByCriteria(criteriaDto, page, size);
            Page<MemberDto> memberDtoPage = memberPage.map(member -> DtoEntityBinder.INSTANCE.toDto(member, MemberDto.class));
            return memberDtoPage;
        } catch (MemberNotFoundException e) {
            log.error("회원 리스트 조회 중 오류 발생: fromId = {} dto = {} page={} size={}",fromId ,criteriaDto,page,size, e);
            throw e;
        }
    }

    @Override
    public MemberRelationshipDto findMemberRelationship(String fromId, String toId) {
        try {
            return DtoEntityBinder.INSTANCE.toDto(memberRepository.findMemberRelationship(fromId, toId)
                            .orElse(null)
                    , MemberRelationshipDto.class);
        } catch (MemberRelationshipNotFoundException e) {
            log.error("회원 관계 조회 중 오류 발생: fromId={}, toId={}", fromId, toId, e);
            throw e;
        }
    }

    @Override
    public Page<MemberRelationshipDto> findMemberRelationshipListByCriteria(String fromId, MemberRelationshipSearchCriteriaDto criteriaDto, int page, int size) {
        try {
            return memberRepository.findMemberRelationshipListByCriteria(criteriaDto, page, size)
                    .map(mr -> DtoEntityBinder.INSTANCE.toDto(mr, MemberRelationshipDto.class));
        } catch (MemberRelationshipNotFoundException e) {
            log.error("회원 관계 리스트 조회 중 오류 발생: fromId={}, dto={}, page={}, size={}", fromId, criteriaDto, page, size, e);
            throw e;
        }
    }

    @Override
    public void updateMember(UpdateMemberDto updateMemberDto) {
        try {
            log.info("회원 업데이트 중");
            memberRepository.updateMember(DtoEntityBinder.INSTANCE.toEntity(updateMemberDto));
        } catch (MemberUpdateException e) {
            log.error("회원 업데이트 중 오류 발생: {}", updateMemberDto, e);
            throw e;
        }
    }

    @Override
    public void updatePassword(UpdatePasswordDto dto) {
        try {
            String memberId = dto.getMemberId();
            String newPassword = dto.getPwd();
            log.info("회원 비밀번호 업데이트 중");
            memberRepository.updatePassword(memberId, newPassword);
        } catch (MemberUpdateException e) {
            log.error("회원 비밀번호 업데이트 중 오류 발생: {}", dto, e);
            throw e;
        }
    }

    //비밀번호 변경
    @Override
    public void updatePhoneNumber(UpdatePhoneNumberDto dto) {
        try {
            String memberId = dto.getMemberId();
            String newPhoneNumber = dto.getPhoneNumber();
            log.info("회원 전화번호 업데이트 중 :  {}", memberId);
            memberRepository.updatePhoneNumber(memberId, newPhoneNumber);
        } catch (MemberUpdateException e) {
            log.error("회원 비밀번호 업데이트 중 오류 발생: {}", dto, e);
            throw e;
        }
    }

    @Override
    public void updateMemberProfilePhotoUrl(UpdateMemberProfilePhotoUrlDto dto) {
        try {
            String memberId = dto.getMemberId();
            String newProfilePhotoUrl = dto.getProfilePhotoUrl();
            log.info("회원 프로필 사진 업데이트 중 : {}", memberId);
            memberRepository.updateMemberProfilePhoto(memberId, newProfilePhotoUrl);
        } catch (MemberUpdateException e) {
            log.error("회원 비밀번호 업데이트 중 오류 발생: {}", dto, e);
            throw e;
        }
    }
    @Override
    public void updateMemberProfileIntro(UpdateMemberProfileIntroDto dto){
        try {
            String memberId = dto.getMemberId();
            String newProfileIntro = dto.getProfileIntro();
            log.info("회원 프로필 소개 업데이트 중 : {}", memberId);
            memberRepository.updateMemberProfileIntro(memberId, newProfileIntro);
        } catch (MemberUpdateException e) {
            log.error("회원 프로필 소개 업데이트 중 오류 발생: {}", dto, e);
            throw e;
        }
    }

    @Override
    public void updateDajungScore(UpdateMemberDajungScoreDto dto) {
        try {
            String memberId = dto.getMemberId();
            int newDajungScore = dto.getDajungScore();

            log.info("회원 다정점수 업데이트 중 : {}", memberId);
            memberRepository.updateMember(
                    memberRepository.findMemberById(memberId)
                            .map(member -> {
                                member.setDajungScore(newDajungScore);
                                return member;
                            }).orElseThrow(() -> new MemberNotFoundException("회원이 없습니다.")));
        } catch (MemberUpdateException e) {
            log.error("회원 다정점수 업데이트 중 오류 발생: {}", dto, e);
            throw e;
        }
    }

    /**
     * 차단해제할거면 dto의 타입에 차단 넣는다.
     *
     * @param dto
     */
    @Override
    public void deleteMemberRelationship(DeleteMemberRelationshipDto dto) {
        try {
            log.info("회원 관계 삭제 중 : {}", dto);
            MemberRelationship mr = DtoEntityBinder.INSTANCE.toEntity(dto);
            mr.setActivated(false);
            memberRepository.updateMemberRelationship(mr);
            log.info("회원 관계 삭제 성공");
        } catch (MemberRelationshipUpdateException e) {
            log.error("회원 관계 삭제 중 오류 발생: {}", dto, e);
            throw e;
        }
    }

    @Override
    public void deleteMember(String memberId) {
        try {
            log.info("회원 삭제 중 : {}", memberId);
            setMemberInitializer(memberId);
            log.info("회원 삭제 성공 : {}", memberId);
        } catch(MemberNotFoundException e) {
            log.error("회원 삭제 중 오류 발생: 회원을 찾을 수 없습니다 {}", memberId, e);
            throw e;
        } catch (MemberUpdateException e) {
            log.error("회원 삭제 중 오류 발생: 회원을 업데이트할 수 없습니다 {}", memberId, e);
            throw e;
        }
    }

    @Override
    public boolean checkNickname(String nickname) {
        log.info("회원 닉네임 중복 확인 완료 : {}", nickname);
        return false;
    }

    @Override
    public boolean checkMemberId(String memberId) {
        log.info("회원 아이디 중복 확인 완료 : {}", memberId);
        return false;
    }

    @Override
    public boolean checkPassword(String memberId, String password) {
        log.info("회원 비밀번호 확인 완료 : {}", memberId);
        return false;
    }

    @Override
    public boolean checkPhoneNumber(String phoneNumber) {
        log.info("회원 전화번호 중복 확인 완료 : {}", phoneNumber);
        return false;
    }

    @Override
    public boolean checkBadWord(String word) {
        return badWordFilterAgent.change(
                        word.replace("*", "")
                        , badWordFilterAgent.getBadWordSeparator()
                )
                .contains("*");
    }

    private void setMemberInitializer(String memberId) {

        String newNickname = RandomData.getRandomNickname();
        String newPhoneNumber = RandomData.getRandomPhoneNumber();
        //계정잠금시간 초기화
        LocalDateTime unlockTime = LocalDateTime.now().plusDays(7);
        while (memberRepository.findMemberByNickname(newNickname).isPresent()) {
            newNickname = RandomData.getRandomNickname();
        }
        while (memberRepository.findMemberByPhoneNumber(newPhoneNumber).isPresent()) {
            newPhoneNumber = RandomData.getRandomPhoneNumber();
        }

        memberRepository.updateMember(Member
                .builder()
                .memberId(memberId)
                .memberRole(Role.MEMBER)
                .nickname(newNickname)
                .profileIntro("")
                .unlockTime(LocalDateTime.of(1, 1, 1, 1, 1, 1))
                .dajungScore(0)
                .memberProfilePublicRange(PublicRange.PRIVATE)
                .allFeedPublicRange(PublicRange.PRIVATE)
                .buildingSubscriptionPublicRange(PublicRange.PRIVATE)
                .receivingAllNotificationAllowed(false)
                .signedOff(true)
                .build());
        memberRepository.updatePhoneNumber(memberId, newPhoneNumber);
        memberRepository.updatePassword(memberId, "null");
        memberRepository.updateMemberProfilePhoto(memberId, null);

//        memberRepository.findMemberRelationshipListByCriteria() 전부 회원관계 찾아내서 activated false로 바꾸기
        memberRepository.findAllMemberRelationshipListByCriteria(MemberRelationshipSearchCriteriaDto.builder().memberId(memberId).build())
                .forEach(mr -> {
                    mr.setActivated(false);
                    memberRepository.updateMemberRelationship(mr);
                });
    }

}
