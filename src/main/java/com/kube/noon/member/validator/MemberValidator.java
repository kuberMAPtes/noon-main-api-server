package com.kube.noon.member.validator;

import com.kube.noon.common.validator.IllegalServiceCallException;
import com.kube.noon.common.validator.ValidationChain;
import com.kube.noon.common.validator.Validator;
import com.kube.noon.member.dto.AddMemberDto;
import com.kube.noon.member.dto.MemberRelationshipDto;
import com.kube.noon.member.dto.UpdatePasswordDto;
import com.kube.noon.member.repository.MemberRepository;
import com.kube.noon.member.service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;

import java.util.regex.Pattern;

@Validator(targetClass = MemberService.class)
public class MemberValidator {

    private static final Pattern PHONE_NUMBER_PATTERN = Pattern.compile("^01(?:0|1|[6-9])-(?:\\d{3}|\\d{4})-\\d{4}$");
    private static final Pattern NICKNAME_PATTERN = Pattern.compile("^[a-zA-Z0-9가-힣_ ]{2,20}$");
    private static final Pattern MEMBER_ID_PATTERN = Pattern.compile("^(?=.*[a-zA-Z])[a-zA-Z0-9_]{6,16}$");
    private static final Pattern PASSWORD_PATTERN = Pattern.compile("^(?=.*[a-zA-Z])(?=.*[0-9])[a-zA-Z0-9!@#\\$%\\^&\\*_]{8,16}$");


    private final ValidationChain validationChain;
    MemberRepository memberRepository;

    @Autowired
    public MemberValidator(ValidationChain validationChain, MemberRepository memberRepository) {
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
                throw new IllegalServiceCallException("형식에 맞지 않은 비밀번호입니다. 형식 : 8~16자,영어와 숫자 포함, 특수문자,대소문자 허용");
            }
            if (dto.getNickname() == null || dto.getNickname().isEmpty()) {
                throw new IllegalServiceCallException("닉네임이 없습니다.");
            }
            if (!NICKNAME_PATTERN.matcher(dto.getNickname()).matches()) {
                throw new IllegalServiceCallException("형식에 맞지 않는 닉네임입니다. 닉네임은 2자 이상 20자 이하여야 합니다.");
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


    public void addMember(AddMemberDto memberDto) {

        System.out.println("밸리데이터 실행");
        validate(memberDto);

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

    public void findMemberByPhoneNumber(String phoneNumber) {

        if(phoneNumber == null || phoneNumber.isEmpty()) {
            throw new IllegalServiceCallException("전화번호가 없습니다.");
        }

        if (!PHONE_NUMBER_PATTERN.matcher(phoneNumber).matches()) {
            throw new IllegalServiceCallException("전화번호 형식이 올바르지 않습니다. 올바른 형식 예: 010-XXXX-XXXX");
        }
    }

    public boolean findMemberById(String memberId) {
        System.out.println("벨리데이터 실행");
        return true;
    }

}