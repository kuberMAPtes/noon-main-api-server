package com.kube.noon.member.service.impl;

import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.kube.noon.common.ObjectStorageAPIProfile;
import com.kube.noon.common.PublicRange;
import com.kube.noon.common.binder.DtoEntityBinder;
import com.kube.noon.feed.service.FeedService;
import com.kube.noon.member.domain.Member;
import com.kube.noon.member.domain.MemberRelationship;
import com.kube.noon.member.dto.ResponseDto.SearchMemberResponseDto;
import com.kube.noon.member.dto.member.*;
import com.kube.noon.member.dto.memberRelationship.*;
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
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Optional;
import java.util.Random;

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
    private final ObjectStorageAPIProfile objectStorageAPIProfile;


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
                member.setPwd("social_sign_up");

                if(dto.getPhoneNumber()==null) {
                    member.setPhoneNumber(RandomData.getRandomPhoneNumber() + "X");
                }else {
                    if (dto.getPhoneNumber().equals("010-0000-0000") || dto.getPhoneNumber().isEmpty()) {
                        member.setPhoneNumber(RandomData.getRandomPhoneNumber() + "X");
                    }
                }
            }
            System.out.println("레포지토리에 넣기 전에 member 검증 : "+member);
            memberRepository.addMember(member);
            log.info("회원 추가 성공 : DTO {}", member);
        } catch (MemberCreationException e) {
            log.error("회원 추가 중 오류 발생: {}", dto, e);
            throw e;
        }
    }

    @Override
    public void addMemberRelationship(AddMemberRelationshipDto dto) {
        try {
            log.info("회원 관계 추가 중: DTO={}", dto);
            dto.setActivated(true);

            MemberRelationship memberRelationship = DtoEntityBinder.INSTANCE.toEntity(dto);

            memberRepository.findMemberRelationship(dto.getFromId(), dto.getToId(), dto.getRelationshipType())
                    .ifPresentOrElse(
                            mr -> {
                                log.info("기존 회원 관계 업데이트 중: 관계 ID={}", mr);
                                memberRepository.updateMemberRelationship(memberRelationship);
                            },
                            () -> {
                                log.info("새로운 회원 관계 추가 중: FromID={}, ToID={}", dto.getFromId(), dto.getToId());
                                memberRepository.addMemberRelationship(memberRelationship);
                            }
                    );

            log.info("회원 관계 추가 성공: DTO={}", memberRelationship);
        } catch (MemberRelationshipCreationException e) {
            log.error("회원 관계 추가 중 오류 발생: DTO={}", dto, e);
            throw e;
        }
    }

    @Override
    public MemberDto findMemberById(String fromId, String memberId) {
        try {
            log.info("회원 조회 중: FromID={}, MemberID={}", fromId, memberId);

            return memberRepository.findMemberById(fromId)
                    .filter(fromMember -> {
                        boolean isAuthorized = fromMember.getMemberRole().equals(Role.ADMIN) || fromId.equals(memberId);
                        log.info("권한 확인: FromID={}, MemberID={}, IsAuthorized={}", fromId, memberId, isAuthorized);
                        return isAuthorized;
                    })
                    .flatMap(fromMember -> memberRepository.findMemberById(memberId)
                            .map(member -> {
                                log.info("회원 정보 변환 중: MemberID={}", memberId);
                                MemberDto memberDto = DtoEntityBinder.INSTANCE.toDto(member, MemberDto.class);
                                log.info("MemberDto: {}", memberDto);
                                return memberDto;
                            }))
                    .orElse(null);

        } catch (MemberNotFoundException e) {
            log.error("회원 조회 중 오류 발생: FromID={}, MemberID={}", fromId, memberId, e);
            throw e;
        }
    }

    @Override
    public Optional<Member> findMemberById(String memberId) {
        try {
            log.info("회원 조회 중: MemberID={}", memberId);
            return memberRepository.findMemberById(memberId);
        } catch (MemberNotFoundException e) {
            log.error("회원 조회 중 오류 발생: MemberID={}", memberId, e);
            throw e;
        }
    }

    @Override
    public ProfileAccessResultDto findMemberProfileById(String fromId, String memberId) {
        try {
            log.info("회원 프로필 조회 중: FromID={}, MemberID={}", fromId, memberId);

            return memberRepository.findMemberById(fromId)
                    .flatMap(fromMember -> {
                        log.info("회원 프로필 처리 중: FromID={}, MemberID={}", fromId, memberId);
                        return handleProfileRetrieval(fromMember, fromId, memberId);
                    }).orElse(new ProfileAccessResultDto(false, "회원 프로필을 찾을 수 없습니다.", null));

        } catch (MemberNotFoundException e) {
            log.error("회원 프로필 조회 중 오류 발생: FromID={}, MemberID={}", fromId, memberId, e);
            return new ProfileAccessResultDto(false, "회원 프로필 조회 중 오류가 발생했습니다.", null);
        }
    }

    private Optional<ProfileAccessResultDto> handleProfileRetrieval(Member fromMember, String fromId, String memberId) {
        log.info("프로필 조회 처리 중: FromID={}, MemberID={}", fromId, memberId);
        if (fromMember.getMemberRole().equals(Role.ADMIN)) {
            log.info("관리자 권한으로 모든 회원의 프로필 조회 중: MemberID={}", memberId);
            return memberRepository.findMemberById(memberId)
                    .map(findedMember -> new ProfileAccessResultDto(true, "프로필 조회 성공", createMemberProfileDto(findedMember, memberId)));
        } else if (fromId.equals(memberId)) {
            log.info("자기 자신을 조회 중: MemberID={}", memberId);
            return memberRepository.findMemberById(memberId)
                    .map(findedMember -> new ProfileAccessResultDto(true, "프로필 조회 성공", createMemberProfileDto(findedMember, memberId)));
        } else {
            log.info("다른 회원의 프로필 조회 중: FromID={}, MemberID={}", fromId, memberId);
            return findOtherMemberProfile(fromId, memberId);
        }
    }

    public ResponseEntity<byte[]> findMemberProfilePhoto(String memberId) {
        Member member = memberRepository.findMemberById(memberId).orElse(null);
        MemberProfileDto memberProfileDto = null;
        if(member != null) {
            memberProfileDto = DtoEntityBinder.INSTANCE.toDto(member, MemberProfileDto.class);
        } else {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        String[] fileNames = memberProfileDto.getProfilePhotoUrl().split("/");
        String fileName = fileNames[fileNames.length - 1];

        S3ObjectInputStream inputStream = objectStorageAPIProfile.getObject(fileName);

        try {
            byte[] imageBytes = inputStream.readAllBytes();
            HttpHeaders headers = new HttpHeaders();
            MediaType mediaType = getMediaType(fileName);
            headers.setContentType(mediaType);

            return new ResponseEntity<>(imageBytes, headers, HttpStatus.OK);
        } catch(IOException e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private MediaType getMediaType(String fileName) {
        String[] parts = fileName.split("\\.");
        String extension = parts[parts.length - 1].toLowerCase();

        switch (extension) {
            case "jpg": case "jpeg":
            case "png": case "gif":
                return MediaType.IMAGE_JPEG;
            case "mp4":
                return MediaType.valueOf("video/mp4");
            default:
                return MediaType.APPLICATION_OCTET_STREAM;
        }
    }

    private Optional<ProfileAccessResultDto> findOtherMemberProfile(String fromId, String memberId) {
        log.info("다른 회원 프로필 조회 중: FromID={}, MemberID={}", fromId, memberId);
        if (fromMemberIsBlocked(fromId, memberId)) {
            log.info("차단된 회원: FromID={}, MemberID={}", fromId, memberId);
            return Optional.of(new ProfileAccessResultDto(false, "차단한 회원의 프로필입니다.", null));
        }

        MemberRelationshipDto memberRelationshipDto = findMemberRelationship(fromId, memberId,RelationshipType.FOLLOW);
        return memberRepository.findMemberById(memberId)
                .map(findedMember -> {
                    PublicRange profilePublicRange = findedMember.getMemberProfilePublicRange();
                    log.info("프로필 공개 범위 확인: MemberID={}, 공개 범위={}", memberId, profilePublicRange);
                    switch (profilePublicRange) {
                        case PUBLIC:
                            log.info("프로필 공개: 누구나 접근 가능");
                            return new ProfileAccessResultDto(true, "프로필에 접근할 수 있습니다.", createMemberProfileDto(findedMember, memberId));
                        case PRIVATE:
                            log.info("프로필 비공개: 접근 불가");
                            return new ProfileAccessResultDto(false, "비공개된 프로필입니다.", null);
                        case FOLLOWER_ONLY:
                            log.info("팔로워 전용 프로필: 접근 가능 여부={}", memberRelationshipDto.getRelationshipType() == RelationshipType.FOLLOW);
                            if (memberRelationshipDto.getRelationshipType() == RelationshipType.FOLLOW) {
                                return new ProfileAccessResultDto(true, "팔로워로서 프로필에 접근할 수 있습니다.", createMemberProfileDto(findedMember, memberId));
                            } else {
                                return new ProfileAccessResultDto(false, "팔로워만 볼 수 있는 프로필입니다.", null);
                            }
                        case MUTUAL_ONLY:
                            boolean isMutual = isMutualFollow(fromId, memberId);
                            log.info("상호 팔로우 전용 프로필: 접근 가능 여부={}", isMutual);
                            if (isMutual) {
                                return new ProfileAccessResultDto(true, "상호 팔로우 관계에서 프로필에 접근할 수 있습니다.", createMemberProfileDto(findedMember, memberId));
                            } else {
                                return new ProfileAccessResultDto(false, "맞팔로우 관계에서만 볼 수 있는 프로필입니다.", null);
                            }
                        default:
                            log.info("기타 경우: 접근 불가");
                            return new ProfileAccessResultDto(false, "프로필을 볼 수 없습니다.", null);
                    }
                });
    }

    public boolean isMutualFollow(String fromId, String memberId) {
        MemberRelationshipDto relationship1 = findMemberRelationship(fromId, memberId, RelationshipType.FOLLOW);
        MemberRelationshipDto relationship2 = findMemberRelationship(memberId, fromId, RelationshipType.FOLLOW);

        if (relationship1 == null || relationship2 == null) {
            return false;
        }

        return relationship1.getRelationshipType() == RelationshipType.FOLLOW &&
                relationship2.getRelationshipType() == RelationshipType.FOLLOW;
    }

    private MemberProfileDto createMemberProfileDto(Member findedMember, String memberId) {
        MemberProfileDto memberProfileDto = DtoEntityBinder.INSTANCE.toDto(findedMember, MemberProfileDto.class);
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

    @Override
    public MemberDto findMemberByNickname(String nickname){
        try {
            log.info("회원 찾는 중 닉네임: {}", nickname);

            return memberRepository.findMemberByNickname(nickname)
                    .map(member -> DtoEntityBinder.INSTANCE.toDto(member, MemberDto.class))
                    .orElseGet(() -> {
                        log.info("회원이 없습니다");
                        return null;
                    });

        } catch (MemberNotFoundException e) {
            log.error("회원 조회 중 오류 발생: 닉네임={}", nickname, e);
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
        MemberRelationshipDto blockRelationshipDto = findMemberRelationship(memberId, fromId,RelationshipType.BLOCK);
        if(blockRelationshipDto == null){
            return false;
        }
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
    public MemberRelationshipDto findMemberRelationship(String fromId, String toId, RelationshipType relationshipType) {
        try {
            log.info("회원 관계 조회 중: fromId={}, toId={}", fromId, toId);

            return memberRepository.findMemberRelationship(fromId, toId, relationshipType)
                    .map(mr -> DtoEntityBinder.INSTANCE.toDto(mr, MemberRelationshipDto.class))
                    .orElse(null);
        } catch (MemberRelationshipNotFoundException e) {
            log.error("회원 관계 조회 중 오류 발생: fromId={}, toId={}", fromId, toId, e);
            throw e;
        }
    }
    @Override
    public MemberRelationshipSimpleDto findMemberRelationshipSimple(String fromId, String toId, RelationshipType relationshipType) {
        try {
            MemberRelationship memberRelationship = memberRepository.findMemberRelationship(fromId, toId, relationshipType)
                    .orElse(null);
            if(memberRelationship != null){
                return DtoEntityBinder.INSTANCE.toDto(
                        DtoEntityBinder.INSTANCE.toDto(memberRelationship, MemberRelationshipDto.class),
                        MemberRelationshipSimpleDto.class
                );
            }else{
                return null;
            }
        } catch (MemberRelationshipNotFoundException e) {
            log.error("회원 관계 조회 중 오류 발생: fromId={}, toId={}", fromId, toId, e);
            throw e;
        }
    }
    @Override
    public FindMemberRelationshipListByCriteriaResponseDto findMemberRelationshipListByCriteria(String fromId, MemberRelationshipSearchCriteriaDto criteriaDto, int page, int size) {
        try {
            FindMemberRelationshipListByCriteriaDto resultDto = memberRepository.findMemberRelationshipListByCriteria(criteriaDto, page, size);

            Page<MemberRelationshipDto> memberRelationshipDtoPage = resultDto.getMemberRelationshipPage()
                    .map(mr -> DtoEntityBinder.INSTANCE.toDto(mr, MemberRelationshipDto.class));

            MemberRelationshipCountDto countDto = resultDto.getMemberRelationshipCountDto();

            return new FindMemberRelationshipListByCriteriaResponseDto(memberRelationshipDtoPage, countDto);
        } catch (MemberRelationshipNotFoundException e) {
            log.error("회원 관계 리스트 조회 중 오류 발생: fromId={}, dto={}, page={}, size={}", fromId, criteriaDto, page, size, e);
            throw e;
        }
    }




    @Override
    public Page<SearchMemberResponseDto> searchMemberByNickname(String requesterId, String searchKeyword, int page) {
        return this.memberRepository.findMemberByNickname(searchKeyword, requesterId, page)
                .map((p) -> SearchMemberResponseDto.of(
                        p,
                        //이쪽 부분 봐주세요 경도님 여기 팔로워만 필요한거지요?
                        this.memberRepository.findMemberRelationship(p.getMemberId(), requesterId,RelationshipType.FOLLOW).isPresent(),
                        this.memberRepository.findMemberRelationship(requesterId, p.getMemberId(),RelationshipType.FOLLOW).isPresent()
                ));
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
    public String updateMemberProfilePhotoUrl2(String memberId, MultipartFile file){
        try{
            String originalFilename = file.getOriginalFilename();
            String Url = objectStorageAPIProfile.putObject(originalFilename, file);

            memberRepository.updateMemberProfilePhoto(memberId, Url);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return memberId;
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
    public void checkNickname(String nickname) {
            log.info("회원 닉네임 중복 확인 완료 : {}", nickname);
    }

    @Override
    public void checkMemberId(String memberId) {
        log.info("회원 아이디 중복 확인 완료 : {}", memberId);
    }
    @Override
    public void checkMemberIdExisted(String memberId){
        log.info("회원 아이디 확인 완료 : {}", memberId);
    }
    //회원에 데이터가 있으면 true
    @Override
    public void checkPhoneNumberAndMemberId(String phoneNumber, String memberId){
        log.info("회원 정보와 입력된 전화번호가 같음을 확인 : {}", phoneNumber);
    }
    @Override
    public void checkLoginMemberIdPattern(String memberId){
        log.info("회원 아이디 패턴 확인 완료 : {}", memberId);
    }
    @Override
    public void checkLoginMemberNotLocked(String memberId){
        log.info("회원 계정 잠금 확인 완료 : {}", memberId);
    }
    @Override
    public void checkNotSocialSignUp(String memberId){
        log.info("회원 소셜 회원가입 확인 완료 : {}", memberId);
    }

    @Override
    public void checkPassword(String memberId, String password) {
        log.info("회원 비밀번호 확인 완료 : {}", memberId);
    }

    //회원에 데이터가 없으면 true
    @Override
    public void checkPhoneNumber(String phoneNumber) {
        log.info("회원 전화번호 중복 확인 완료 : {}", phoneNumber);
    }

    @Override
    public void checkBadWord(String word) {
        log.info("회원 단어 확인 완료 : {}", word);
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
