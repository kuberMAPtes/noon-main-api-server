package com.kube.noon.member.validator;

import com.kube.noon.common.validator.IllegalServiceCallException;
import com.kube.noon.common.validator.ValidationChain;
import com.kube.noon.member.dto.*;
import com.kube.noon.member.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.regex.Pattern;

@Component
public class MemberValidationRule {

    private static final Pattern PHONE_NUMBER_PATTERN = Pattern.compile("^01(?:0|1|[6-9])-(?:\\d{3}|\\d{4})-\\d{4}$");
    private static final Pattern NICKNAME_PATTERN = Pattern.compile("^[a-zA-Z0-9가-힣_ ]{2,20}$");
    private static final Pattern MEMBER_ID_PATTERN = Pattern.compile("^(?=.*[a-zA-Z])[a-zA-Z0-9_]{6,16}$");
    private static final Pattern PASSWORD_PATTERN = Pattern.compile("^(?=.*[a-zA-Z])(?=.*[0-9])[a-zA-Z0-9!@#\\$%\\^&\\*_]{8,16}$");


    private final MemberScanner memberScanner;
    private final ValidationChain validationChain;
    private final MemberRepository memberRepository;

    @Autowired
    public MemberValidationRule(ValidationChain validationChain, MemberRepository memberRepository, MemberScanner memberScanner) {
        this.validationChain = validationChain;
        this.memberRepository = memberRepository;
        this.memberScanner = memberScanner;
    }


    /**
     * memberScanner.scanDtoField()가 내부적으로 사용한다.
     * service를 실행하면 validator작동되고 scanner가 Rule을 사용한다.
     */
    @EventListener(ApplicationReadyEvent.class)
    public void setRule() {

        validationChain.addRule(AddMemberDto.class, dto -> {

            System.out.println("DTO 좀 보자 :::: " + dto);
            System.out.println("DTO 좀 보자 :::: " + dto.getClass());

            memberScanner.scanIsDataNull(dto.getMemberId());
            memberScanner.scanIsMemberAlreadyExist(dto.getMemberId());
            memberScanner.scanMemberIdPattern(dto.getMemberId());

            memberScanner.scanIsDataNull(dto.getNickname());
            memberScanner.scanNicknameIsAlreadyExist(dto.getNickname());
            memberScanner.scanNicknamePattern(dto.getNickname());

            memberScanner.scanIsDataNull(dto.getPhoneNumber());
            memberScanner.scanPhoneNumberIsAlreadyExist(dto.getPhoneNumber());
            memberScanner.scanPhoneNumberPattern(dto.getPhoneNumber());

            if (Boolean.FALSE.equals(dto.getSocialSignUp())) {
                memberScanner.scanIsDataNull(dto.getPwd());
                memberScanner.scanPasswordPattern(dto.getPwd());
            }
        }
        );

        validationChain.addRule(AddMemberRelationshipDto.class, dto -> {

            memberScanner.scanIsDataNull(dto.getFromId());
            memberScanner.scanIsDataNull(dto.getToId());
            memberScanner.scanIsDataNull(dto.getRelationshipType());

            memberScanner.scanIsSameMember(dto.getFromId(), dto.getToId());

            //이미 관계가 있는지 찾아서 관계가 있으면 있다고 하고
            //관계가 없다면 없다고 해야함
            memberScanner.scanIsMemberExist(dto.getFromId());
            memberScanner.scanIsMemberExist(dto.getToId());
            /**
             * 내 dto와 db의 도메인이 Boolean빼고는 다 같으면 activated를 True로 바꾸고 update한다.
             * 내 dto와 도메인의 RelationshipType이 다르면 받은 타입으로 update한다.
             * fromid,toid로 확인된 관계가 없으면 add한다.
             */



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

        validationChain.addRule(UpdateMemberDto.class, dto -> {

        });

        validationChain.addRule(DeleteMemberRelationshipDto.class, dto -> {

        });


    }//end of setRule

    public <T>void scanDataIsNull(T data){
        if (data == null) {
            throw new IllegalServiceCallException("받은 데이터가 없습니다.");
        }

        if (data instanceof String && ((String) data).trim().isEmpty()) {
            throw new IllegalServiceCallException("받은 데이터가 없습니다.");
        }
    }



}
