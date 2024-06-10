package com.kube.noon.member.service.impl;

import com.kube.noon.common.binder.DtoEntityBinder;
import com.kube.noon.common.messagesender.NotificationCoolSmsMessageSender;
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
    public void sendAuthentificationNumber(String phoneNumber){

        MemberDto dto = memberService.findMemberByPhoneNumber(phoneNumber);


        String messageFormat = "인증번호 : ";
        String authNumber = RandomData.getRandomAuthNumber();
        String message = messageFormat + authNumber;

        //Redis에 인증번호 저장
        authRepository.createAuthentificationNumber(phoneNumber, authNumber);


        notificationCoolSmsMessageSender.send(
                DtoEntityBinder.INSTANCE.toEntity(dto),message
        );

        log.info("인증번호 전송 완료 : {} 보낸 메세지 : {}", phoneNumber, message);
    }


    //인증번호검증하기
    public boolean confirmAuthenticationNumber(String phoneNumber, String randomNumber) {

        boolean isMatched = false;


        if (isVerify(phoneNumber,randomNumber)) {
            isMatched = true;
        }
        authRepository.deleteAuthentificationNumber(phoneNumber);

        return isMatched;
    }
    //인증번호검증도구
    private boolean isVerify(String phoneNumber, String randomNumber) {
        //레디스에 키가 있는지 본다. 사용자가 준 폰 번호를 레디스의 키로 써서 Value(인증번호)가 있는지 본다.
        //그 인증번호가 사용자가 준 인증번호랑 같은지 본다.
        return (authRepository.hasKey(phoneNumber) &&
                authRepository.getAuthentificationNumber(phoneNumber).equals(randomNumber));
    }

}
