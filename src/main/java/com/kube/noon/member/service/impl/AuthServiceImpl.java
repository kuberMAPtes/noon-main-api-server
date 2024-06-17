package com.kube.noon.member.service.impl;

import com.kube.noon.common.binder.DtoEntityBinder;
import com.kube.noon.common.messagesender.NotificationCoolSmsMessageSender;
import com.kube.noon.member.domain.Member;
import com.kube.noon.member.dto.member.MemberDto;
import com.kube.noon.member.dto.util.RandomData;
import com.kube.noon.member.repository.AuthRepository;
import com.kube.noon.member.service.AuthService;
import com.kube.noon.member.service.MemberService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Service
@Slf4j
@Transactional
public class AuthServiceImpl implements AuthService {


    @Value("${cool-sms.access-key}")
    private String apiKey;

    @Value("${cool-sms.secret-key}")
    private String apiSecret;

    @Value("${cool-sms.from-phone-number}")
    private String fromNumber;

    private final AuthRepository authRepository;
    private final MemberService memberService;
    private final NotificationCoolSmsMessageSender notificationCoolSmsMessageSender;



    @Autowired
    public AuthServiceImpl( AuthRepository authRepository, MemberService memberService, NotificationCoolSmsMessageSender notificationCoolSmsMessageSender) {
        this.authRepository = authRepository;
        this.memberService = memberService;
        this.notificationCoolSmsMessageSender = notificationCoolSmsMessageSender;
    }

    @Override
    public boolean sendAuthentificationNumber(String phoneNumber){

        if(memberService.findMemberByPhoneNumber(phoneNumber) != null){
            log.info("이미 가입된 회원이므로 가입하실 수 없습니다. : {}", phoneNumber);
            throw new RuntimeException("이미 가입된 회원이므로 가입하실 수 없습니다.");
        }

        String messageFormat = "인증번호 : ";
        String authNumber = RandomData.getRandomAuthNumber();
        String message = messageFormat + authNumber;

        //Redis에 인증번호 저장
        authRepository.createAuthentificationNumber(phoneNumber, authNumber);


       MemberDto memberDto = MemberDto.builder()
               .phoneNumber(phoneNumber)
               .build();
        notificationCoolSmsMessageSender.send(
                DtoEntityBinder.INSTANCE.toEntity(memberDto),message
        );

        log.info("인증번호 전송 완료 : {} 보낸 메세지 : {}", phoneNumber, message);

        return true;
    };


    //인증번호검증하기
    public Map<String,Object> confirmAuthenticationNumber(String phoneNumber, String inputNumber) {

        String storedNumber = authRepository.getAuthentificationNumber(phoneNumber);
        String returnMessage = "";
        String messageKey = "message";
        String resultKey = "result";

        if(storedNumber==null){
            System.out.println("인증번호를 보내지 않았거나 만료되었습니다.");
            returnMessage = "인증번호를 보내지 않았거나 만료되었습니다.";
            return Map.of(messageKey,returnMessage,resultKey,false);
        }
        if(!storedNumber.equals(inputNumber)){
            authRepository.incrementFailedAttempts(phoneNumber);
            int attempts = authRepository.getFailedAttempts(phoneNumber);
            if(attempts>= authRepository.getMaxAttempts()){
                System.out.println("인증번호 시도횟수 초과로 인증번호가 삭제되었습니다.");
                returnMessage = "인증번호 시도횟수 초과로 인증번호가 삭제되었습니다.";
                return Map.of(messageKey,returnMessage,resultKey,false);
            }else {
                System.out.println("인증번호가 일치하지 않습니다. 현재 시도 횟수 : " + attempts);
                returnMessage = "인증번호가 일치하지 않습니다. 현재 시도 횟수 : " + attempts;
                return Map.of(messageKey,returnMessage,resultKey,false);
            }
        }else{
            authRepository.deleteAuthentificationNumber(phoneNumber);
            System.out.println("인증 성공");
            returnMessage = "인증 성공";
            return Map.of(messageKey,returnMessage,"result",true);
        }
    }

}
