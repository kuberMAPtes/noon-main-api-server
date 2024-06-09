package com.kube.noon.member.controller;

import com.kube.noon.member.dto.*;
import com.kube.noon.member.enums.LoginFlag;
import com.kube.noon.member.service.LoginAttemptCheckerAgent;
import com.kube.noon.member.service.MemberService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

@Slf4j
@RestController
@RequestMapping("/member")
public class MemberRestController {

    @Autowired
    @Qualifier("memberServiceImpl")
    private MemberService memberService;

//    @Autowired
//    private SampleService sampleService;

    @Autowired
    @Qualifier("loginAttemptCheckerAgent")
    private LoginAttemptCheckerAgent loginAttemptCheckerAgent;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    // Constructor
    public MemberRestController() {
        System.out.println("생성자 :: " + this.getClass());
    }

    // Common
    private void checkBadWord(String word) {
        if (memberService.checkBadWord(word)) {
            String bodyString = "비속어는 사용할 수 없습니다.";
            ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(bodyString);
        }
    }
    // Method

    @PostMapping("/sendAuthentificationNumber")
    public ResponseEntity<?> sendAuthentificationNumber(@RequestParam String phoneNumber) {
        try {

//            memberService.sendAuthenticationNumber(phoneNumber);
            return ResponseEntity.ok("Authentication number sent to " + phoneNumber);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to send authentication number");
        }
    }

    @PostMapping("/confirmAuthentificationNumber")
    public ResponseEntity<?> confirmAuthentificationNumber(@RequestParam String phoneNumber, @RequestParam String authNumber) {

//        boolean isConfirmed = memberService.confirmAuthenticationNumber(phoneNumber, authNumber);
        if (true) {
            return ResponseEntity.ok("Authentication confirmed");
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid authentication number");
        }
    }

    //체크 : 완료
    @GetMapping("/checkMemberId")
    public ResponseEntity<?> checkMemberId(@RequestParam String memberId) {
        boolean isOk = memberService.checkMemberId(memberId);

        checkBadWord(memberId);

        return ResponseEntity.ok(isOk ? "Member ID is duplicated" : "Member ID is available");
    }

    //체크 : 완료
    @GetMapping("/checkNickname")
    public ResponseEntity<?> checkNickname(@RequestParam String nickname) {
        boolean isOk = memberService.checkNickname(nickname);

        checkBadWord(nickname);

        return ResponseEntity.ok(isOk ? "Nickname is duplicated" : "Nickname is available");
    }

    //체크 : 완료
    @GetMapping("/checkPhoneNumber")
    public ResponseEntity<?> checkPhoneNumber(@RequestParam String phoneNumber) {
        boolean isOk = memberService.checkPhoneNumber(phoneNumber);

        checkBadWord(phoneNumber);

        return ResponseEntity.ok(isOk ? "Phone number is duplicated" : "Phone number is available");
    }


    //체크 : 완료
    @GetMapping("/checkPassword")
    public ResponseEntity<?> checkPassword(@RequestParam String memberId, @RequestParam String password) {
        boolean isOk = memberService.checkPassword(memberId, password);

        checkBadWord(password);

        if (isOk) {
            return ResponseEntity.ok("패스워드 사용 가능");
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("부적합한 패스워드");
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequestDto dto) {

        //아이디와 비밀번호를 검증한다. 아이디를 검증한다.
        String id = dto.getMemberId();

        AtomicReference<LoginFlag> isCorrect = new AtomicReference<>(LoginFlag.FAILURE);

        MemberDto memberDto = memberService.findMemberById(id,id);

        if(memberDto == null) {
            isCorrect.set(LoginFlag.INCORRECT_ID);
        } else {
            if (memberDto.getPwd().equals(dto.getPwd())) {
                isCorrect.set(LoginFlag.SUCCESS);
            } else {
                isCorrect.set(LoginFlag.INCORRECT_PASSWORD);
            }
        }

        if (isCorrect.get().equals(LoginFlag.SUCCESS)) {

            String jwt = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ0ZXN0IiwiaWF0IjoxNjI5MzQwNjYwLCJleHAiOjE2MjkzNDA2NjB9.1";
            loginAttemptCheckerAgent.loginSucceeded(id);

            return ResponseEntity.status(HttpStatus.OK).body(jwt);
        } else if (isCorrect.get().equals(LoginFlag.INCORRECT_ID)) {

            loginAttemptCheckerAgent.loginFailed(id);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("존재하지 않는 아이디입니다.");

        } else if (isCorrect.get().equals(LoginFlag.INCORRECT_PASSWORD)) {
            loginAttemptCheckerAgent.loginFailed(id);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("비밀번호가 틀림");
        } else {
            loginAttemptCheckerAgent.loginFailed(id);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인 실패");
        }
    }

    public ResponseEntity<?> kakaoLogin(@RequestParam String memberId, @RequestParam String password) {
        return null;
    }

    public ResponseEntity<?> naverLogin(@RequestParam String memberId, @RequestParam String password) {
        return null;
    }

    public ResponseEntity<?> googleLogin(@RequestParam String memberId, @RequestParam String password) {
        return null;
    }

    public ResponseEntity<?> logout(@RequestParam String memberId) {
        return null;
    }

    // 체크 : 완료
    @PostMapping("/addMember")
    public ResponseEntity<?> addMember(@Valid @RequestBody AddMemberDto dto, BindingResult bindingResult) {
//        try {
            System.out.println("왜 로그가 안찍혀");
            if (bindingResult.hasErrors()) {
                System.out.println("여기는 머야");
                Map<String, String> errors = new HashMap<>();
                bindingResult.getFieldErrors().forEach(fieldError -> {
                    errors.put(fieldError.getField(), fieldError.getDefaultMessage());
                });
                System.out.println(errors);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
            }

            System.out.println("왜 로그가 안찍혀");
            memberService.addMember(dto);

            return ResponseEntity.ok("회원가입 성공");
//        }
//        catch (Exception e) {
//            return ResponseEntity.status(HttpStatus.CONFLICT).body("회원이 이미 존재합니다.");
//        }
    }

    @PostMapping("/updatePassword")
    public ResponseEntity<?> updatePassword() {

        System.out.println("샘플서비스실행");
//        sampleService.func1();
        System.out.println("샘플서비스실행완료");
//        memberService.findMemberById("member_1");

//        memberService.updatePassword(memberId, newPassword);

        return null;
    }

    public ResponseEntity<?> updatePhoneNumber(@RequestParam String memberId, @RequestParam String newPassword) {
        memberService.updatePhoneNumber(memberId, newPassword);
        return ResponseEntity.ok("전화번호 변경 성공");
    }

    @GetMapping("/updateProfilePhoto")
    public ResponseEntity<?> updateProfilePhoto() {
//
//        MemberProfileDto memberProfileDto = new MemberProfileDto();
//        memberProfileDto.setMemberId("test");
//        memberProfileDto.setProfilePhotoUrl("test");
//        memberProfileDto.setNickname("얍얍얍얍");
//        redisTemplate.opsForValue().set("abc", memberProfileDto);
//
//        MemberProfileDto message = (MemberProfileDto) redisTemplate.opsForValue().get("abc");
//
//        System.out.println(message.getProfilePhotoUrl());
//        System.out.println(message);
//
//        Map<String, Object> map = new HashMap<>();
//
//        map.put("message", message);
//
//        map.put("message2", memberService.findMemberById("member_1"));

        return null;
    }

    public ResponseEntity<?> updateProfileIntro(@RequestParam String memberId, @RequestParam String newProfileIntro) {

//        memberService.updateMember(
//                memberService.findMemberById(memberId).map(
//                        (member)->{
//                            member.setProfileIntro(newProfileIntro);
//
//                            return MemberBinder.INSTANCE.toDto(member, MemberProfileDto.class);
//                        }).orElseGet(()->{
//                            return null;
//                        })
//        );
        return null;
    }

    public ResponseEntity<?> updateDajungScore(@RequestParam String memberId, @RequestParam int newDajungScore) {
        memberService.updateDajungScore(memberId, newDajungScore);
        return ResponseEntity.ok("다정점수 변경 성공");
    }

    /**
     * 관리자가 사용
     * @param memberId
     * @return
     */
    @GetMapping("/getMember/{fromId}/{memberId}/")
    public ResponseEntity<?> getMember(@PathVariable String fromId, @PathVariable String memberId) {

        MemberDto dto = null;
        MemberDto fromMemberDto = memberService.findMemberById(fromId,memberId);



        return ResponseEntity.ok(dto);
    }

    @GetMapping("/getMemberProfile/{fromId}/{memberId}")
    public ResponseEntity<MemberProfileDto> getMemberProfile(@PathVariable String fromId, @PathVariable String memberId) {

        MemberProfileDto dto = null;
        MemberDto fromMemberDto = memberService.findMemberById(fromId,memberId);
        MemberRelationshipDto memberRelationshipDto = memberService.findMemberRelationship(fromId, memberId);
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/listMember/{memberId}")
    public ResponseEntity<?> ListMember(@PathVariable String memberId) {

        MemberSearchCriteriaDto memberSearchCriteriaDto = MemberSearchCriteriaDto.builder().memberId(memberId).build();

//        memberService.findMemberListByCriteria(null, 0, 0);
        return null;
    }

    public ResponseEntity<?> updateMember(@RequestParam String memberId) {
        return null;
    }

    public ResponseEntity<?> deleteMember(@RequestParam String memberId) {
        return null;
    }

    public ResponseEntity<?> addMemberRelationship(@RequestParam String memberId) {
        return null;
    }

    public ResponseEntity<?> getMemberRelationshipList(@RequestParam String memberId) {
        return null;
    }

    public ResponseEntity<?> deleteMemberRelationship(@RequestParam String memberId) {
        return null;
    }


}
