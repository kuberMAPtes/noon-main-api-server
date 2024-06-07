package com.kube.noon.member.validator;

import com.kube.noon.common.validator.IllegalServiceCallException;
import com.kube.noon.common.validator.ValidationChain;
import com.kube.noon.common.validator.Validator;
import com.kube.noon.member.dto.AddMemberDto;
import com.kube.noon.member.dto.MemberRelationshipDto;
import com.kube.noon.member.dto.UpdatePasswordDto;
import com.kube.noon.member.repository.MemberRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;

import java.util.regex.Pattern;

@Slf4j
@Validator(targetClass = SsampleServiceImpl.class)
public class SsampleValidator {

    private static final Pattern PHONE_NUMBER_PATTERN = Pattern.compile("^01(?:0|1|[6-9])-(?:\\d{3}|\\d{4})-\\d{4}$");
    private static final Pattern NICKNAME_PATTERN = Pattern.compile("^[a-zA-Z0-9가-힣_ ]{2,20}$");
    private static final Pattern MEMBER_ID_PATTERN = Pattern.compile("^(?=.*[a-zA-Z])[a-zA-Z0-9_]{6,16}$");
    private static final Pattern PASSWORD_PATTERN = Pattern.compile("^(?=.*[a-zA-Z])(?=.*[0-9])[a-zA-Z0-9!@#\\$%\\^&\\*_]{8,16}$");


    private final ValidationChain<Object> validationChain;
    private final MemberRepository memberRepository;

    @Autowired
    public SsampleValidator(ValidationChain<Object> validationChain, MemberRepository memberRepository) {
        this.validationChain = validationChain;
        this.memberRepository = memberRepository;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void setRule() {

        validationChain.addRule(AddMemberDto.class, dto -> {

            System.out.println("DTO 좀 보자 :::: " + dto);
            System.out.println("DTO 좀 보자 :::: " + dto.getClass());

            if (dto == null) {
                throw new IllegalServiceCallException("회원 정보가 없습니다.");
            }
            if (dto.getMemberId() == null || dto.getMemberId().isEmpty()) {
                throw new IllegalServiceCallException("회원 아이디가 없습니다.");
            }
            if (!MEMBER_ID_PATTERN.matcher(dto.getMemberId()).matches()) {
                throw new IllegalServiceCallException("회원 아이디는 6자 이상 16자 이하여야 합니다.");
            }
            if (memberRepository.findMemberById(dto.getMemberId()).isPresent()) {
                throw new IllegalServiceCallException("이미 존재하는 회원 아이디입니다.");
            }
            if (memberRepository.findMemberByNickname(dto.getNickname()).isPresent()) {
                throw new IllegalServiceCallException("이미 존재하는 닉네임입니다.");
            }
            if (memberRepository.findMemberByPhoneNumber(dto.getPhoneNumber()).isPresent()) {
                throw new IllegalServiceCallException("이미 존재하는 전화번호입니다.");
            }
            if (!PASSWORD_PATTERN.matcher(dto.getPwd()).matches()) {
                throw new IllegalServiceCallException("비밀번호는 8자 이상 16자 이하여야 합니다.");
            }
            if (dto.getNickname() == null || dto.getNickname().isEmpty()) {
                throw new IllegalServiceCallException("닉네임이 없습니다.");
            }
            if (!NICKNAME_PATTERN.matcher(dto.getNickname()).matches()) {
                throw new IllegalServiceCallException("닉네임은 2자 이상 20자 이하여야 합니다.");
            }
            if (dto.getPhoneNumber() == null || dto.getPhoneNumber().isEmpty()) {
                throw new IllegalServiceCallException("전화번호가 없습니다.");
            }
            if (!PHONE_NUMBER_PATTERN.matcher(dto.getPhoneNumber()).matches()) {
                throw new IllegalServiceCallException("전화번호 형식이 올바르지 않습니다. 올바른 형식 예: 010-XXXX-XXXX");
            }

            if (Boolean.TRUE.equals(dto.getSocialSignUp())) {
                dto.setPwd("social_sign_up");
            }
        });

        validationChain.addRule(UpdatePasswordDto.class, dto -> {

            if (dto.getMemberId() == null || dto.getMemberId().isEmpty()) {
                throw new IllegalServiceCallException("회원 아이디가 없습니다.");
            }
            if (dto.getPwd() == null || dto.getPwd().isEmpty()) {
                throw new IllegalServiceCallException("새로운 비밀번호가 없습니다.");
            }
            if (!PASSWORD_PATTERN.matcher(dto.getPwd()).matches()) {
                throw new IllegalServiceCallException("비밀번호는 8자 이상 16자 이하여야 합니다.");
            }
        });

        validationChain.addRule(MemberRelationshipDto.class, obj -> {

        });

    }//end of setRule

    public void validate(Object dto) {
        System.out.println("VALIDATE 실행되었습니다.");
        validationChain.validate(dto);
    }

    public boolean func1() {
        log.info("Validator-func1");
        System.out.println("작동이 됐다아아");

        AddMemberDto addMemberDto = AddMemberDto.builder()
                .memberId("member_10000")
                .pwd("abc12341")
                .nickname("밥은 먹었니")
                .phoneNumber("010-9453-1049").build();
        validate(addMemberDto);
        System.out.println("유효성 통과했고 디비에 넣지는 않음");
        return true;
    }

    public boolean func2() {
        log.info("Validator-func2");
        return false;
    }

    public boolean func3() {
        log.info("Validator-func3");
        throw new IllegalCallerException();
    }

    public boolean func33() {
        log.info("Not called");
        return true;
    }

    public boolean funcWithParameter(Integer param) {
        log.info("Validator-funcWithParameter");
        return true;
    }

    public boolean funcWithParameterNotProceed(Integer param) {
        log.info("Validator-funcWithParameterNotProceed");
        return param < 13;
    }
}
