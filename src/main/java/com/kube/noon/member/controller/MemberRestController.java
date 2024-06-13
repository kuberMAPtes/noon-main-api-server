package com.kube.noon.member.controller;

import com.kube.noon.common.binder.DtoEntityBinder;
import com.kube.noon.common.security.SecurityConstants;
import com.kube.noon.common.security.TokenPair;
import com.kube.noon.common.security.authentication.authtoken.TokenType;
import com.kube.noon.common.security.support.BearerTokenSupport;
import com.kube.noon.member.dto.RequestDto.LoginRequestDto;
import com.kube.noon.member.dto.RequestDto.MemberRelationshipSearchCriteriaRequestDto;
import com.kube.noon.member.dto.RequestDto.MemberSearchCriteriaRequestDto;
import com.kube.noon.member.dto.auth.googleLoginRequestDto;
import com.kube.noon.member.dto.member.*;
import com.kube.noon.member.dto.memberRelationship.AddMemberRelationshipDto;
import com.kube.noon.member.dto.memberRelationship.DeleteMemberRelationshipDto;
import com.kube.noon.member.dto.memberRelationship.MemberRelationshipDto;
import com.kube.noon.member.dto.search.MemberRelationshipSearchCriteriaDto;
import com.kube.noon.member.dto.search.MemberSearchCriteriaDto;
import com.kube.noon.member.enums.LoginFlag;
import com.kube.noon.member.enums.RelationshipType;
import com.kube.noon.member.service.AuthService;
import com.kube.noon.member.service.KakaoService;
import com.kube.noon.member.service.LoginAttemptCheckerAgent;
import com.kube.noon.member.service.MemberService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 음? 왜 바로 ok만 내보내지? -> GlobalExceptionHandler에서 검증하고 있음
 * 음? 왜 memberService 에서 그냥 ok만 내보내지? ->memberValidator, memberValidationRule,memberScanner 에서 검증하고 있음
 * <p>
 * 잘못된 설계가 하나 있음 RequestDto로 쓰이는 Dto중에 FromId가 존재하는 것들이 있음. RequestDto에서 FromId를 꺼내서 쓰면 절대 안됨 비었음.
 */
@Slf4j
@RestController
@RequestMapping("/member")
public class MemberRestController {

    private final MemberService memberService;

    private final LoginAttemptCheckerAgent loginAttemptCheckerAgent;

    private final KakaoService kakaoService;

    private final AuthService authService;
    private final List<BearerTokenSupport> tokenSupport;

    @Value("${pageUnit}")
    int pageUnit;

    @Value("${pageSize}")
    int pageSize;


    // Constructor
    public MemberRestController(@Qualifier("memberServiceImpl") MemberService memberService,
                                @Qualifier("loginAttemptCheckerAgent") LoginAttemptCheckerAgent loginAttemptCheckerAgent,
                                KakaoService kakaoService,
                                AuthService authService,
                                List<BearerTokenSupport> tokenSupport) {
        this.authService = authService;
        log.info("생성자 :: " + this.getClass());
        this.kakaoService = kakaoService;
        this.memberService = memberService;
        this.loginAttemptCheckerAgent = loginAttemptCheckerAgent;
        this.tokenSupport = tokenSupport;
    }


    @GetMapping("/sendAuthentificationNumber")
    public ResponseEntity<ApiResponse<Void>> sendAuthentificationNumber(@RequestParam String phoneNumber) {
        log.info("sendAuthentificationNumber :: " + phoneNumber);
        authService.sendAuthentificationNumber(phoneNumber);
        return ResponseEntity.ok(ApiResponseFactory.createResponse(phoneNumber + "로 인증 번호가 전송되었습니다.", null));
    }

    @GetMapping("/confirmAuthentificationNumber")
    public ResponseEntity<ApiResponse<Void>> confirmAuthentificationNumber(@RequestParam String phoneNumber, @RequestParam String authNumber) {
        log.info("confirmAuthentificationNumber :: " + phoneNumber + " " + authNumber);
        boolean isConfirmed = authService.confirmAuthenticationNumber(phoneNumber, authNumber);
        if (isConfirmed) {
            return ResponseEntity.ok(ApiResponseFactory.createResponse("인증이 확인되었습니다.", null));
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ApiResponseFactory.createErrorResponse("잘못된 인증 번호입니다."));
        }
    }

    @GetMapping("/checkMemberId")
    public ResponseEntity<ApiResponse<String>> checkMemberId(@RequestParam String memberId) {
        log.info("checkMemberId :: " + memberId);
        memberService.checkMemberId(memberId);
        memberService.checkBadWord(memberId);
        return ResponseEntity.ok(ApiResponseFactory.createResponse("회원 ID를 사용할 수 있습니다.", null));
    }

    @GetMapping("/checkNickname")
    public ResponseEntity<ApiResponse<String>> checkNickname(@RequestParam String nickname) {
        log.info("checkNickname :: " + nickname);
        memberService.checkNickname(nickname);
        memberService.checkBadWord(nickname);
        return ResponseEntity.ok(ApiResponseFactory.createResponse("닉네임을 사용할 수 있습니다.", null));
    }

    @GetMapping("/checkPhoneNumber")
    public ResponseEntity<ApiResponse<String>> checkPhoneNumber(@RequestParam String phoneNumber) {
        log.info("checkPhoneNumber :: " + phoneNumber);
        memberService.checkPhoneNumber(phoneNumber);
        return ResponseEntity.ok(ApiResponseFactory.createResponse("전화번호를 사용할 수 있습니다.", null));
    }

    @GetMapping("/checkPassword")
    public ResponseEntity<ApiResponse<String>> checkPassword(@RequestParam String memberId, @RequestParam String password) {
        log.info("checkPassword :: " + memberId + " " + password);
        memberService.checkPassword(memberId, password);
        return ResponseEntity.ok(ApiResponseFactory.createResponse("패스워드를 사용할 수 있습니다.", null));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<MemberDto>> login(@RequestBody LoginRequestDto dto, HttpServletResponse response) {
        log.info("로그인 요청: {}", dto);
        String memberId = dto.getMemberId();
        MemberDto memberDto = memberService.findMemberById(memberId, memberId);

        LoginFlag loginFlag = LoginFlag.FAILURE;
        if (memberDto == null) {
            log.info("존재하지 않는 아이디: {}", memberId);
            loginFlag = LoginFlag.INCORRECT_ID;
        } else {
            if (memberDto.getPwd().equals(dto.getPwd())) {
                log.info("로그인 성공: {}", memberId);
                loginFlag = LoginFlag.SUCCESS;
            } else {
                log.info("비밀번호 불일치: {} {} 원래 아이디 비번 : {} {} ", memberId, dto.getPwd(), memberDto.getMemberId(), memberDto.getPwd());
                loginFlag = LoginFlag.INCORRECT_PASSWORD;
            }
        }

        String errorMessage = "";
        switch (loginFlag) {
            case SUCCESS:
//                this.loginAttemptCheckerAgent.loginSucceeded(memberId); // TODO: With Redis
                log.info("로그인 성공 처리 완료: {}", memberId);


                TokenPair tokenPair = this.tokenSupport.stream()
                        .filter((tokenSupport) -> tokenSupport.supports(TokenType.NATIVE_TOKEN))
                        .findAny()
                        .orElseThrow()
                        .generateToken(memberId);
                response.addCookie(wrapWithCookie(SecurityConstants.ACCESS_TOKEN_COOKIE_KEY.get(), tokenPair.getAccessToken()));
                response.addCookie(wrapWithCookie(SecurityConstants.REFRESH_TOKEN_COOKIE_KEY.get(), tokenPair.getRefreshToken()));
                response.addCookie(wrapWithCookie(SecurityConstants.TOKEN_TYPE_COOKIE_KEY.get(), TokenType.NATIVE_TOKEN.name()));
                return ResponseEntity.ok()
                        .body(ApiResponseFactory.createResponse("로그인 성공", memberDto));
            case INCORRECT_ID:
                errorMessage = "존재하지 않는 아이디입니다.";
                log.info("존재하지 않는 아이디: {}", memberId);
                break;
            case INCORRECT_PASSWORD:
                errorMessage = "비밀번호가 일치하지 않습니다.";
                log.info("비밀번호 불일치: {}", memberId);
                break;
            default:
                errorMessage = "로그인 실패";
                log.info("로그인 실패: {}", memberId);
        }

//        this.loginAttemptCheckerAgent.loginFailed(memberId); // TODO: With Redis
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(ApiResponseFactory.createErrorResponse(errorMessage));
    }

    /**
     * /*
     * * HTTP/1.1 200 OK
     * Content-Type: application/json;charset=UTF-8
     * {
     * "token_type":"bearer",
     * "access_token":"${ACCESS_TOKEN}",
     * "expires_in":43199,
     * "refresh_token":"${REFRESH_TOKEN}",
     * "refresh_token_expires_in":5184000,
     * "scope":"account_email profile"
     * }
     * 1. 토큰얻기 서비스 사용(사용자가 카카오로그인인증을 마쳤다는거임)
     * 2. 얻은 토큰으로 멤버정보얻기
     * 3. 얻은 정보로 처음이면 회원가입시키기, 있으면 로그인
     */
    @RequestMapping(value = "kakaoLogin", method = RequestMethod.GET)
    public ResponseEntity<ApiResponse<String>> kakaoLogin(@RequestParam(value = "code") String authorizeCode,
                                                          HttpServletResponse response) throws Exception {
        log.info("카카오 로그인 요청: code={}", authorizeCode);

        TokenPair tokenPair = this.kakaoService.generateTokenPairAndAddMemberIfNotExists(authorizeCode);
        response.addCookie(wrapWithCookie(SecurityConstants.ACCESS_TOKEN_COOKIE_KEY.get(), tokenPair.getAccessToken()));
        response.addCookie(wrapWithCookie(SecurityConstants.REFRESH_TOKEN_COOKIE_KEY.get(), tokenPair.getRefreshToken()));
        response.addCookie(wrapWithCookie(SecurityConstants.TOKEN_TYPE_COOKIE_KEY.get(), TokenType.KAKAO_TOKEN.name()));

        final String redirectClientUrl = "http://127.0.0.1:3000/member/kakaoNav?loginWay=" + "kakao";
        return ResponseEntity.status(HttpStatus.PERMANENT_REDIRECT)
                .header(HttpHeaders.LOCATION, redirectClientUrl)
                .build();
    }

    @PostMapping("/googleLogin")
    public ResponseEntity<?> googleLogin(@RequestBody googleLoginRequestDto dto) {
//        log.info("googleLogin" + memberId + " " + authorizeCode);
        return null;
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(
            @CookieValue(value = "token_type", required = false) String tokenTypeStr,
            @CookieValue(value = "refresh_token", required = false) String refreshToken,
            HttpServletResponse response
    ) {
        response.addCookie(getDestructionCookie(SecurityConstants.ACCESS_TOKEN_COOKIE_KEY.get()));
        response.addCookie(getDestructionCookie(SecurityConstants.REFRESH_TOKEN_COOKIE_KEY.get()));
        response.addCookie(getDestructionCookie(SecurityConstants.TOKEN_TYPE_COOKIE_KEY.get()));

        if (tokenTypeStr != null) {
            try {
                TokenType tokenType = TokenType.valueOf(tokenTypeStr);
                this.tokenSupport.forEach((ts) -> {
                    if (ts.supports(tokenType) && refreshToken != null) {
                        ts.invalidateRefreshToken(refreshToken);
                    }
                });
            } catch (IllegalArgumentException e) {
                log.trace("No such token type={}", tokenTypeStr, e);
            }
        }

        return ResponseEntity.ok(ApiResponseFactory.createResponse("로그아웃 성공", null));
    }

    private Cookie getDestructionCookie(String key) {
        Cookie cookie = new Cookie(key, null);
        cookie.setMaxAge(0);
        cookie.setPath("/");
        return cookie;
    }

    // 체크 : 완료
    @PostMapping("/addMember")
    public ResponseEntity<ApiResponse<?>> addMember(@Valid @RequestBody AddMemberDto dto, BindingResult bindingResult) {
        log.info("addMember" + dto + " " + bindingResult);
        log.info("왜 로그가 안찍혀");
        if (bindingResult.hasErrors()) {
            log.info("여기는 머야");
            Map<String, String> errors = new HashMap<>();
            bindingResult.getFieldErrors().forEach(fieldError -> {
                errors.put(fieldError.getField(), fieldError.getDefaultMessage());
            });
            log.info(errors.toString());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponseFactory.createErrorResponse("회원가입 실패", errors.toString()));
        }

        log.info("왜 로그가 안찍혀");
        memberService.addMember(dto);

        return ResponseEntity.ok(ApiResponseFactory.createResponse("회원가입 성공", null));
    }

    @PostMapping("/updatePassword")
    public ResponseEntity<ApiResponse<Void>> updatePassword(@RequestBody UpdatePasswordDto requestDto) {
        log.info("updatePassword" + requestDto);
        memberService.updatePassword(requestDto);
        return ResponseEntity.ok(ApiResponseFactory.createResponse("비밀번호 변경 성공", null));
    }

    @PostMapping("/updatePhoneNumber")
    public ResponseEntity<ApiResponse<Void>> updatePhoneNumber(@RequestBody UpdatePhoneNumberDto requestDto) {
        log.info("updatePhoneNumber" + requestDto);
        memberService.updatePhoneNumber(requestDto);
        return ResponseEntity.ok(ApiResponseFactory.createResponse("전화번호 변경 성공", null));
    }

    @PostMapping("/updateProfilePhoto")
    public ResponseEntity<ApiResponse<Void>> updateProfilePhoto(@RequestBody UpdateMemberProfilePhotoUrlDto requestDto) {
        log.info("updateProfilePhoto" + requestDto);
        memberService.updateMemberProfilePhotoUrl(requestDto);
        return ResponseEntity.ok(ApiResponseFactory.createResponse("프로필 사진 변경 성공", null));
    }

    @PostMapping("/updateProfileIntro")
    public ResponseEntity<ApiResponse<Void>> updateProfileIntro(@RequestBody UpdateMemberProfileIntroDto requestDto) {
        log.info("updateProfileIntro" + requestDto);
        memberService.updateMemberProfileIntro(requestDto);
        return ResponseEntity.ok(ApiResponseFactory.createResponse("프로필 소개 변경 성공", null));
    }

    @PostMapping("/updateDajungScore")
    public ResponseEntity<ApiResponse<Void>> updateDajungScore(@RequestBody UpdateMemberDajungScoreDto requestDto) {
        log.info("updateDajungScore" + requestDto);
        memberService.updateDajungScore(requestDto);
        return ResponseEntity.ok(ApiResponseFactory.createResponse("다정점수 변경 성공", null));
    }

    /**
     * 관리자가 사용
     */
    @GetMapping("/getMember/{fromId}/{memberId}/")
    public ResponseEntity<ApiResponse<MemberDto>> getMember(@PathVariable String fromId, @PathVariable String memberId) {
        log.info("getMember" + fromId + " " + memberId);
        MemberDto dto = memberService.findMemberById(fromId, memberId);
        return ResponseEntity.ok(ApiResponseFactory.createResponse("회원 조회 성공", dto));
    }

    @GetMapping("/getMemberProfile/{fromId}/{toId}")
    public ResponseEntity<ApiResponse<MemberProfileDto>> getMemberProfile(@PathVariable String fromId, @PathVariable String toId) {
        log.info("getMemberProfile" + fromId + " " + toId);
        MemberProfileDto dto = memberService.findMemberProfileById(fromId, toId);
        return ResponseEntity.ok(ApiResponseFactory.createResponse("회원 프로필 조회 성공", dto));
    }

    @PostMapping("/listMember")
    public ResponseEntity<Page<MemberDto>> listMember(@RequestBody MemberSearchCriteriaRequestDto requestDto) {
        log.info("listMember" + requestDto);
        MemberSearchCriteriaDto memberSearchCriteriaDto = DtoEntityBinder.INSTANCE.toOtherDto(requestDto);
        Page<MemberDto> members = memberService.findMemberListByCriteria(requestDto.getMemberId(), memberSearchCriteriaDto, pageUnit, pageSize);
        return ResponseEntity.ok(members);
    }

    @PostMapping("/deleteMember/{memberId}")
    public ResponseEntity<ApiResponse<Void>> deleteMember(@PathVariable String memberId) {
        log.info("deleteMember" + memberId);
        memberService.deleteMember(memberId);
        return ResponseEntity.ok(ApiResponseFactory.createResponse("회원이 성공적으로 삭제되었습니다.", null));
    }

    @PostMapping("/addMemberRelationship")
    public ResponseEntity<ApiResponse<String>> addMemberRelationship(@RequestBody AddMemberRelationshipDto dto) {
        log.info("addMemberRelationship" + dto);
        memberService.addMemberRelationship(dto);
        return ResponseEntity.ok(
                ApiResponseFactory.createResponse("관계가 성공적으로 추가되었습니다.", null)
        );
    }

    @PostMapping("/getMemberRelationshipList")
    public ResponseEntity<Page<MemberRelationshipDto>> getMemberRelationshipList(@RequestBody MemberRelationshipSearchCriteriaRequestDto requestDto) {
        log.info("getMemberRelationshipList" + requestDto);
        MemberRelationshipSearchCriteriaDto memberRelationshipSearchCriteriaDto = DtoEntityBinder.INSTANCE.toOtherDto(requestDto);
        Page<MemberRelationshipDto> relationships = memberService.findMemberRelationshipListByCriteria(requestDto.getFromId(), memberRelationshipSearchCriteriaDto, pageUnit, pageSize);
        return ResponseEntity.ok(relationships);
    }

    @PostMapping("/deleteMemberRelationship/{fromId}/{toId}")
    public ResponseEntity<ApiResponse<Void>> deleteMemberRelationship(@RequestBody DeleteMemberRelationshipDto requestDto) {
        log.info("deleteMemberRelationship" + requestDto);
        // JWT 토큰 검증
        String fromId = validateJwtToken(RequestContext.getAuthorization());
        requestDto.setFromId(fromId);
        memberService.deleteMemberRelationship(requestDto);
        String message = requestDto.getRelationshipType() == RelationshipType.FOLLOW ? "팔로우가 해제되었습니다." : "차단이 해제되었습니다.";
        return ResponseEntity.ok(ApiResponseFactory.createResponse(message, null));
    }

    //이게
    private String validateJwtToken(String token) {
        // JWT 토큰 검증 로직 구현
        // 유효한 토큰이면 사용자 ID를 반환하고, 그렇지 않으면 예외를 던집니다.
        return "member_100"; // 예시로 사용자 ID 반환
    }

    private Cookie wrapWithCookie(String cookieName, String value) {
        Cookie cookie = new Cookie(cookieName, value);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        return cookie;
    }
}
