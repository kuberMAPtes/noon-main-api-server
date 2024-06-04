package com.kube.noon.member.controller;

import com.kube.noon.member.dto.AddMemberDto;
import com.kube.noon.member.dto.LoginRequestDto;
import com.kube.noon.member.enums.LoginFlag;
import com.kube.noon.member.service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.atomic.AtomicReference;

@RestController
@RequestMapping("/member/*")
public class MemberRestController {

    @Autowired
    @Qualifier("memberServiceImpl")
    private MemberService memberService;

    // Constructor
    public MemberRestController() {
        System.out.println("생성자 :: " + this.getClass());
    }

    // Common
    private ResponseEntity<?> checkBadWord(String word){
        if(memberService.checkBadWord(word)){
            String bodyString = "비속어는 사용할 수 없습니다.";
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(bodyString);
        }
        return null;
    }
    // Method

    @PostMapping("/sendAuthentificationNumber")
    public ResponseEntity<?> sendAuthentificationNumber(@RequestParam String email) {
        try {
//            memberService.sendAuthenticationNumber(email);
            return ResponseEntity.ok("Authentication number sent to " + email);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to send authentication number");
        }
    }

    @PostMapping("/confirmAuthentificationNumber")
    public ResponseEntity<?> confirmAuthentificationNumber(@RequestParam String email, @RequestParam String authNumber) {

//        boolean isConfirmed = memberService.confirmAuthenticationNumber(email, authNumber);
        if (true) {
            return ResponseEntity.ok("Authentication confirmed");
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid authentication number");
        }
    }

    @PostMapping("/addMember")
    public ResponseEntity<?> addMember(@RequestBody AddMemberDto dto) {
        try {
            memberService.addMember(dto);
            return ResponseEntity.ok("Member added successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Member already exists");
        }
    }

    @GetMapping("/checkNickname")
    public ResponseEntity<?> checkNickname(@RequestParam String nickname) {
        boolean isOk = memberService.checkNickname(nickname);

        checkBadWord(nickname);

        return ResponseEntity.ok(isOk ? "Nickname is duplicated" : "Nickname is available");
    }

    @GetMapping("/checkMemberId")
    public ResponseEntity<?> checkMemberId(@RequestParam String memberId) {
        boolean isOk = memberService.checkMemberId(memberId);

        checkBadWord(memberId);

        return ResponseEntity.ok(isOk ? "Member ID is duplicated" : "Member ID is available");
    }

    @GetMapping("/checkPassword")
    public ResponseEntity<?> checkPassword(@RequestParam String memberId, @RequestParam String password) {
        boolean isOk = memberService.checkPassword(memberId, password);

        checkBadWord(password);

        if (isOk) {
            return ResponseEntity.ok("Password is correct");
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Incorrect password");
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequestDto dto) {

        //아이디와 비밀번호를 검증한다. 아이디를 검증한다.
        String id = dto.getMemberId();

        AtomicReference<LoginFlag> isCorrect = new AtomicReference<>(LoginFlag.FAILURE);

        memberService.findMemberById(id).ifPresentOrElse(
                member -> {
                    if (member.getPwd().equals(dto.getPwd())) {
                        isCorrect.set(LoginFlag.SUCCESS);
                    } else {
                        isCorrect.set(LoginFlag.INCORRECT_PASSWORD);
                    }
                },
                () -> {
                    isCorrect.set(LoginFlag.INCORRECT_ID);
                });
        if(isCorrect.get().equals(LoginFlag.SUCCESS)) {

            String jwt ="eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ0ZXN0IiwiaWF0IjoxNjI5MzQwNjYwLCJleHAiOjE2MjkzNDA2NjB9.1";

            return ResponseEntity.status(HttpStatus.OK).body(jwt);
        }else if(isCorrect.get().equals(LoginFlag.INCORRECT_ID)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("존재하지 않는 아이디입니다.");
        }else if(isCorrect.get().equals(LoginFlag.INCORRECT_PASSWORD)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("비밀번호가 틀림");
        }else{
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인 실패");
        }
    }




}
