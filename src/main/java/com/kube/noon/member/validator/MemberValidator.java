package com.kube.noon.member.validator;

import com.kube.noon.common.validator.IllegalServiceCallException;
import com.kube.noon.common.validator.ValidationChain;
import com.kube.noon.common.validator.Validator;
import com.kube.noon.common.binder.DtoEntityBinder;
import com.kube.noon.member.domain.Member;
import com.kube.noon.member.dto.AddMemberDto;
import com.kube.noon.member.dto.MemberRelationshipDto;
import com.kube.noon.member.dto.MemberSearchCriteriaDto;
import com.kube.noon.member.exception.MemberSecurityBreachException;
import com.kube.noon.member.repository.MemberRepository;
import com.kube.noon.member.service.impl.MemberServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.regex.Pattern;

@Validator(targetClass = MemberServiceImpl.class)
@Slf4j
public class MemberValidator {

    private static final Pattern PHONE_NUMBER_PATTERN = Pattern.compile("^01(?:0|1|[6-9])-(?:\\d{3}|\\d{4})-\\d{4}$");
    private static final Pattern NICKNAME_PATTERN = Pattern.compile("^[a-zA-Z0-9가-힣_ ]{2,20}$");
    private static final Pattern MEMBER_ID_PATTERN = Pattern.compile("^(?=.*[a-zA-Z])[a-zA-Z0-9_]{6,16}$");
    private static final Pattern PASSWORD_PATTERN = Pattern.compile("^(?=.*[a-zA-Z])(?=.*[0-9])[a-zA-Z0-9!@#\\$%\\^&\\*_]{8,16}$");


    private final ValidationChain validationChain;
    private final MemberRepository memberRepository;

    @Autowired
    public MemberValidator(ValidationChain validationChain, MemberRepository memberRepository) {
        this.validationChain = validationChain;
        this.memberRepository = memberRepository;
    }

    private <T> void validate(T dto) {
        System.out.println("VALIDATE 실행되었습니다.");
        checkMemberisSignedOff(
                ((Member)DtoEntityBinder.INSTANCE.toEntity(dto)).getMemberId()
        );
        validationChain.validate(dto);
    }
    private <T> void isNull(T args){
        if(args == null){
            throw new IllegalServiceCallException("받은 데이터가 없습니다.");
        }
    }

    private void checkMemberisSignedOff(String memberId) {
        memberRepository.findMemberById(memberId)
                .ifPresent(member -> {
                    if (member.getSignedOff()) {
                        throw new MemberSecurityBreachException("탈퇴한 회원입니다.");
                    }
                });
    }

    //완료
    //먼저 키가 Controller딴을 통과해서 키를 받으면
    //Validation로직 즉시 통과하는 로직 구현
    public void addMember(AddMemberDto memberDto) {
        System.out.println("밸리데이터 실행");
        isNull(memberDto);
        validate(memberDto);
    }
    //자기자신과는할수없지
    //소스회원 대상회원이 존재해야지 널이 아니여야지
    //자기 자신과 관계를 맺을 수 없지
    //회원 탈퇴한 사람과 맺을 수 없지
    //isEmpty인지
    //내가 차단햇어 팔로우 햇어
    //팔로우 햇어 차단햇어 다 add야 차단취소는 delete야

    //Dto 받았어
    //도메인을 꺼냈어
    //내 dto와 db의 도메인이 Boolean빼고는 다 같으면 activated를 True로 바꾸면 대

    //내 dto와 도메인이 relationshipType이 달라? 그러면 Type을 받은대로 바꾸면 돼.

    //delete는? dto와 domain이 Boolean빼고 다 같으면 activated True로 바꾸면 돼
    //나머지는 에러야
    public void addMemberRelationship(MemberRelationshipDto memberRelationshipDto) {
        System.out.println("밸리데이터 실행");
        isNull(memberRelationshipDto);
        validate(memberRelationshipDto);
    }

    public void findMemberById(String memberId) {
        isNull(memberId);
    }

    public void findMemberProfileById(String memberId) {
        isNull(memberId);
    }

    public void findMemberByNickname(String nickname) {
        isNull(nickname);
        if (!NICKNAME_PATTERN.matcher(nickname).matches()) {
            throw new IllegalServiceCallException("형식에 맞지 않는 닉네임입니다. 닉네임은 2자 이상 20자 이하여야 합니다.");
        }
    }

    public void findMemberByPhoneNumber(String phoneNumber) {
        isNull(phoneNumber);
        if (!PHONE_NUMBER_PATTERN.matcher(phoneNumber).matches()) {
            throw new IllegalServiceCallException("전화번호 형식이 올바르지 않습니다. 올바른 형식 예: 010-XXXX-XXXX");
        }
    }

    public void findMemberListByCriiteria(MemberSearchCriteriaDto searchDto) {
        if (searchDto == null) {
            throw new IllegalServiceCallException("검색 조건이 없습니다.");
        }
        validate(searchDto);
    }


    public void updatePassword(String memberId, String newPassword) {
        if (memberId == null || memberId.isEmpty()) {
            throw new IllegalServiceCallException("회원 아이디가 없습니다.");
        }
        if (newPassword == null || newPassword.isEmpty()) {
            throw new IllegalServiceCallException("새로운 비밀번호가 없습니다.");
        }

        memberRepository.findMemberById(memberId).orElseThrow(() -> new IllegalServiceCallException("존재하지 않는 회원입니다."));

    }

    public void updatePhoneNumber(String memberId, String newPassword) {
        if (memberId == null || memberId.isEmpty()) {
            throw new IllegalServiceCallException("회원 아이디가 없습니다.");
        }
        if (newPassword == null || newPassword.isEmpty()) {
            throw new IllegalServiceCallException("새로운 비밀번호가 없습니다.");
        }

        memberRepository.findMemberById(memberId).orElseThrow(() -> new IllegalServiceCallException("존재하지 않는 회원입니다."));

    }

    public void updateMemberProfilePhoto(String memberId, String newProfilePhotoUrl) {
        if (memberId == null || memberId.isEmpty()) {
            throw new IllegalServiceCallException("회원 아이디가 없습니다.");
        }
        if (newProfilePhotoUrl == null || newProfilePhotoUrl.isEmpty()) {
            throw new IllegalServiceCallException("새로운 프로필 사진이 없습니다.");
        }

        memberRepository.findMemberById(memberId).orElseThrow(() -> new IllegalServiceCallException("존재하지 않는 회원입니다."));

    }

    public void updateDajungScore(String memberId, int dajungScore) {
        if (memberId == null || memberId.isEmpty()) {
            throw new IllegalServiceCallException("회원 아이디가 없습니다.");
        }
        if (dajungScore < 0) {
            throw new IllegalServiceCallException("다정 점수는 0 이상이어야 합니다.");
        }

        memberRepository.findMemberById(memberId).orElseThrow(() -> new IllegalServiceCallException("존재하지 않는 회원입니다."));

    }

    public void deleteMemberRelationship(MemberRelationshipDto dto) {
        if (dto == null) {
            throw new IllegalServiceCallException("회원 관계 정보가 없습니다.");
        }
    }

    public void deleteMember(String memberId) {
        if (memberId == null || memberId.isEmpty()) {
            throw new IllegalServiceCallException("회원 아이디가 없습니다.");
        }
    }

    public void checkNickname(String nickname) {
        if (nickname == null || nickname.isEmpty()) {
            throw new IllegalServiceCallException("닉네임이 없습니다.");
        }
        if (!NICKNAME_PATTERN.matcher(nickname).matches()) {
            throw new IllegalServiceCallException("형식에 맞지 않는 닉네임입니다. 닉네임은 2자 이상 20자 이하여야 합니다.");
        }
    }

    public void checkMemberId(String memberId) {
        if (memberId == null || memberId.isEmpty()) {
            throw new IllegalServiceCallException("회원 아이디가 없습니다.");
        }
        if (!MEMBER_ID_PATTERN.matcher(memberId).matches()) {
            throw new IllegalServiceCallException("회원 아이디는 6자 이상 16자 이하여야 합니다.");
        }
    }

    public void checkPassword(String email, String password) {
        if (email == null || email.isEmpty()) {
            throw new IllegalServiceCallException("이메일이 없습니다.");
        }
        if (password == null || password.isEmpty()) {
            throw new IllegalServiceCallException("비밀번호가 없습니다.");
        }
        if (!PASSWORD_PATTERN.matcher(password).matches()) {
            throw new IllegalServiceCallException("비밀번호는 8자 이상 16자 이하여야 합니다.");
        }
    }

    public void checkPhoneNumber(String phoneNumber) {
        if (phoneNumber == null || phoneNumber.isEmpty()) {
            throw new IllegalServiceCallException("전화번호가 없습니다.");
        }
        if (!PHONE_NUMBER_PATTERN.matcher(phoneNumber).matches()) {
            throw new IllegalServiceCallException("전화번호 형식이 올바르지 않습니다. 올바른 형식 예: 010-XXXX-XXXX");
        }
    }

    public void checkBadWord(String word) {
        if (word == null || word.isEmpty()) {
            log.info("욕설이 없습니다");
        }
    }



}