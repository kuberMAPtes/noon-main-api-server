package com.kube.noon.member.controller;

import com.kube.noon.common.binder.DtoEntityBinder;
import com.kube.noon.member.dto.*;
import com.kube.noon.member.dto.RequestDto.MemberRelationshipSearchCriteriaRequestDto;
import com.kube.noon.member.dto.RequestDto.MemberSearchCriteriaRequestDto;
import com.kube.noon.member.enums.LoginFlag;
import com.kube.noon.member.enums.RelationshipType;
import com.kube.noon.member.service.LoginAttemptCheckerAgent;
import com.kube.noon.member.service.MemberService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
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

    @Value("${pageUnit}")
    int pageUnit;

    @Value("${pageSize}")
    int pageSize;


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
    }

    @PostMapping("/updatePassword")
    public ResponseEntity<?> updatePassword(@RequestBody UpdatePasswordDto requestDto) {
        memberService.updatePassword(requestDto);
        return ResponseEntity.ok("비밀번호 변경 성공");
    }

    @PostMapping("/updateNickname")
    public ResponseEntity<?> updatePhoneNumber(@RequestBody UpdatePhoneNumberDto requestDto) {
        memberService.updatePhoneNumber(requestDto);
        return ResponseEntity.ok("전화번호 변경 성공");
    }

    @PostMapping("/updateProfilePhoto")
    public ResponseEntity<?> updateProfilePhoto(@RequestBody UpdateMemberProfilePhotoUrlDto requestDto) {
        memberService.updateMemberProfilePhoto(requestDto);
        return ResponseEntity.ok("프로필 사진 변경 성공");
    }
    @PostMapping("/updateProfileIntro")
    public ResponseEntity<?> updateProfileIntro(@RequestBody UpdateMemberProfileIntroDto requestDto) {
        //String memberId, Stirng ProfileIntro
        memberService.updateMemberProfileIntro(requestDto);
        return ResponseEntity.ok("프로필 소개 변경 성공");
    }
    @PostMapping("/updateDajungScore")
    public ResponseEntity<?> updateDajungScore(@RequestBody UpdateDajungScoreDto requestDto) {
        //@RequestParam String memberId, @RequestParam int newDajungScore
        memberService.updateDajungScore(requestDto);
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

    @GetMapping("/getMemberProfile/{fromId}/{toId}")
    public ResponseEntity<MemberProfileDto> getMemberProfile(@PathVariable String fromId, @PathVariable String toId) {

        MemberProfileDto dto = null;
        MemberDto fromMemberDto = memberService.findMemberById(fromId, toId);
        MemberRelationshipDto memberRelationshipDto = memberService.findMemberRelationship(fromId, toId);
        return ResponseEntity.ok(dto);
    }

    @PostMapping("/listMember")
    public ResponseEntity<Page<MemberDto>> listMember(@RequestBody MemberSearchCriteriaRequestDto requestDto) {
        MemberSearchCriteriaDto memberSearchCriteriaDto = DtoEntityBinder.INSTANCE.toOtherDto(requestDto);
        Page<MemberDto> members = memberService.findMemberListByCriteria(requestDto.getMemberId(), memberSearchCriteriaDto, pageUnit, pageSize);
        return ResponseEntity.ok(members);
    }

    @PostMapping("/deleteMember/{memberId}")
    public ResponseEntity<ApiResponse<Void>> deleteMember(@PathVariable String memberId) {
        memberService.deleteMember(memberId);
        ApiResponse<Void> response = ApiResponseFactory.createResponse("회원이 성공적으로 삭제되었습니다.", null);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/addMemberRelationship")
    public ResponseEntity<String> addMemberRelationship(@RequestBody AddMemberRelationshipDto dto) {
        memberService.addMemberRelationship(dto);
        return ResponseEntity.ok("회원 관계가 성공적으로 추가되었습니다.");
    }

    @PostMapping("/getMemberRelationshipList")
    public ResponseEntity<Page<MemberRelationshipDto>> getMemberRelationshipList(@RequestBody MemberRelationshipSearchCriteriaRequestDto requestDto) {
        MemberRelationshipSearchCriteriaDto memberRelationshipSearchCriteriaDto = DtoEntityBinder.INSTANCE.toOtherDto(requestDto);
        Page<MemberRelationshipDto> relationships = memberService.findMemberRelationshipListByCriteria(requestDto.getFromId(), memberRelationshipSearchCriteriaDto, pageUnit, pageSize);
        return ResponseEntity.ok(relationships);
    }

    @PostMapping("/deleteMemberRelationship/{fromId}/{toId}")
    public ResponseEntity<ApiResponse<Void>> deleteMemberRelationship(@RequestBody DeleteMemberRelationshipDto requestDto) {
        // JWT 토큰 검증
        String fromId = validateJwtToken(RequestContext.getAuthorization());

        requestDto.setFromId(fromId);
        memberService.deleteMemberRelationship(requestDto);
        String message = requestDto.getRelationshipType() == RelationshipType.FOLLOW ? "팔로우가 해제되었습니다." : "차단이 해제되었습니다.";
        ApiResponse<Void> response = ApiResponseFactory.createResponse(message, null);
        return ResponseEntity.ok(response);
    }

    private String validateJwtToken(String token) {
        // JWT 토큰 검증 로직 구현
        // 유효한 토큰이면 사용자 ID를 반환하고, 그렇지 않으면 예외를 던집니다.
        return "member_100"; // 예시로 사용자 ID 반환
    }


}
