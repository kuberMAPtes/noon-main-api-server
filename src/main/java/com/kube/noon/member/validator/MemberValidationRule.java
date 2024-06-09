package com.kube.noon.member.validator;

import com.kube.noon.common.validator.ValidationChain;
import com.kube.noon.member.dto.*;
import com.kube.noon.member.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class MemberValidationRule {

    private final MemberScanner memberScanner;
    private final ValidationChain validationChain;

    @Autowired
    public MemberValidationRule(ValidationChain validationChain, MemberRepository memberRepository, MemberScanner memberScanner) {
        this.validationChain = validationChain;
        this.memberScanner = memberScanner;
    }


    /**
     * memberScanner.imoDtoFieldO()가 내부적으로 사용한다.
     * service를 실행하면 validator작동되고 scanner가 Rule을 사용한다.
     */
    @EventListener(ApplicationReadyEvent.class)
    public void setRule() {

        validationChain.addRule(AddMemberDto.class, dto -> {

            System.out.println("DTO 좀 보자 :::: " + dto);
            System.out.println("DTO 좀 보자 :::: " + dto.getClass());

            memberScanner.imoDataNotNull(dto.getMemberId());
            memberScanner.imoMemberIdNotExist(dto.getMemberId());
            memberScanner.imoMemberIdPatternO(dto.getMemberId());

            memberScanner.imoDataNotNull(dto.getNickname());
            memberScanner.imoNicknameNotAlreadyExist(dto.getNickname());
            memberScanner.imoNicknamePatternO(dto.getNickname());

            memberScanner.imoDataNotNull(dto.getPhoneNumber());
            memberScanner.imoPhoneNumberNotAlreadyExist(dto.getPhoneNumber());
            memberScanner.imoPhoneNumberPatternO(dto.getPhoneNumber());

            if (Boolean.FALSE.equals(dto.getSocialSignUp())) {
                memberScanner.imoDataNotNull(dto.getPwd());
                memberScanner.imoPwdPatternO(dto.getPwd());
            }
        }
        );

        validationChain.addRule(AddMemberRelationshipDto.class, dto -> {

            memberScanner.imoDataNotNull(dto.getFromId());
            memberScanner.imoDataNotNull(dto.getToId());
            memberScanner.imoDataNotNull(dto.getRelationshipType());
            memberScanner.imoMemberNotSame(dto.getFromId(), dto.getToId());
            /**
             * 내 dto와 db의 도메인이 Boolean빼고는 다 같으면 activated를 True로 바꾸고 update한다.
             * 내 dto와 도메인의 RelationshipType이 다르면 받은 타입으로 update한다.
             * fromid,toid로 확인된 관계가 없으면 add한다.
             */



        });

        //비밀번호를 변경 ~~하면 비밀번호변경 안되어야해
        //일단 회원이 접속하지 않았으면 변경이 안되어야해는 jwt키
        //본인이 아니라면 변경이 안되어야해
        //형식이
        validationChain.addRule(UpdatePasswordDto.class, dto -> {
            memberScanner.imoDataNotNull(dto.getMemberId());
            memberScanner.imoDataNotNull(dto.getPwd());
            memberScanner.imoPwdPatternO(dto.getPwd());
        });

        //둘중하나
        validationChain.addRule(UpdateMemberDto.class, dto -> {
            memberScanner.imoTwoDataNotNullSimul(dto.getMemberId(), dto.getNickname());

            if(dto.getMemberId()!=null) {
                memberScanner.imoMemberIdPatternO(dto.getMemberId());
            }
            if(dto.getNickname()!=null){
                memberScanner.imoNicknamePatternO(dto.getNickname());
            }
            if(dto.getDajungScore()!=null){
                memberScanner.imoDajungScorePatternO(dto.getDajungScore());
            }
            if(dto.getProfileIntro()!=null){
                memberScanner.imoProfileIntroPatternO(dto.getProfileIntro());
            }
            if(dto.getUnlockTime()!=null){
                memberScanner.imoUnlockTimePatternO(dto.getUnlockTime());
            }
            //레인지는 정할 룰이 없다.
            //알람설정은 정할 룰이 없다.

        });

        validationChain.addRule(DeleteMemberRelationshipDto.class, dto -> {
            memberScanner.imoDataNotNull(dto.getFromId());
            memberScanner.imoDataNotNull(dto.getToId());
            memberScanner.imoDataNotNull(dto.getRelationshipType());
            memberScanner.imoMemberNotSame(dto.getFromId(), dto.getToId());
            memberScanner.imoMemberRelationshipExist(dto.getFromId(),dto.getToId());


        });


    }//end of setRule



}
