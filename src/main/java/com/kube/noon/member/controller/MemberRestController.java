package com.kube.noon.member.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kube.noon.common.binder.DtoEntityBinder;
import com.kube.noon.member.dto.RequestDto.LoginRequestDto;
import com.kube.noon.member.dto.RequestDto.MemberRelationshipSearchCriteriaRequestDto;
import com.kube.noon.member.dto.RequestDto.MemberSearchCriteriaRequestDto;
import com.kube.noon.member.dto.auth.KakaoResponseDto;
import com.kube.noon.member.dto.auth.googleLoginRequestDto;
import com.kube.noon.member.dto.member.*;
import com.kube.noon.member.dto.memberRelationship.AddMemberRelationshipDto;
import com.kube.noon.member.dto.memberRelationship.DeleteMemberRelationshipDto;
import com.kube.noon.member.dto.memberRelationship.MemberRelationshipDto;
import com.kube.noon.member.dto.search.MemberRelationshipSearchCriteriaDto;
import com.kube.noon.member.dto.search.MemberSearchCriteriaDto;
import com.kube.noon.member.dto.util.RandomData;
import com.kube.noon.member.enums.LoginFlag;
import com.kube.noon.member.enums.RelationshipType;
import com.kube.noon.member.service.AuthService;
import com.kube.noon.member.service.KakaoService;
import com.kube.noon.member.service.LoginAttemptCheckerAgent;
import com.kube.noon.member.service.MemberService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;


import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import reactor.netty.http.server.HttpServerResponse;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

/**
 * 음? 왜 바로 ok만 내보내지? -> GlobalExceptionHandler에서 검증하고 있음
 * 음? 왜 memberService 에서 그냥 ok만 내보내지? ->memberValidator, memberValidationRule,memberScanner 에서 검증하고 있음
 *
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

    @Value("${pageUnit}")
    int pageUnit;

    @Value("${pageSize}")
    int pageSize;


    // Constructor
    public MemberRestController(@Qualifier("memberServiceImpl") MemberService memberService
            , @Qualifier("loginAttemptCheckerAgent") LoginAttemptCheckerAgent loginAttemptCheckerAgent
            , KakaoService kakaoService
            , AuthService authService) {
        this.authService = authService;
        log.info("생성자 :: " + this.getClass());
        this.kakaoService = kakaoService;
        this.memberService = memberService;
        this.loginAttemptCheckerAgent = loginAttemptCheckerAgent;
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
    @GetMapping("/checkCookies")
    public ResponseEntity<ApiResponse<Boolean>> checkCookies(HttpServletRequest request) {
        Optional<String> authorizationCookie = Arrays.stream(request.getCookies())
                .filter(cookie -> "Authorization".equals(cookie.getName()))
                .map(cookie -> cookie.getValue())
                .findFirst();

        Optional<String> refreshTokenCookie = Arrays.stream(request.getCookies())
                .filter(cookie -> "Refresh-Token".equals(cookie.getName()))
                .map(cookie -> cookie.getValue())
                .findFirst();

        //authorizeCookie가 없고 refreshToken도 없으면 쿠키가 존재하지 않는다고 알린다.
        //authorizeCookie

        boolean isExist = authorizationCookie.isPresent() && refreshTokenCookie.isPresent();

        String message =  isExist ? "쿠키가 존재합니다." : "쿠키가 존재하지 않습니다.";

        return ResponseEntity.ok(ApiResponseFactory.createResponse(message, isExist));
    }
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<MemberDto>> login(@RequestBody LoginRequestDto dto,HttpServletResponse response) {
        log.info("로그인 요청: {}", dto);
        String id = dto.getMemberId();
        AtomicReference<LoginFlag> isCorrect = new AtomicReference<>(LoginFlag.FAILURE);
        MemberDto memberDto = memberService.findMemberById(id, id);

        if (memberDto == null) {
            log.info("존재하지 않는 아이디: {}", id);
            isCorrect.set(LoginFlag.INCORRECT_ID);
        } else {
            if (memberDto.getPwd().equals(dto.getPwd())) {
                log.info("로그인 성공: {}", id);
                isCorrect.set(LoginFlag.SUCCESS);
            } else {
                log.info("비밀번호 불일치: {} {} 원래 아이디 비번 : {} {} ", id, dto.getPwd(), memberDto.getMemberId(), memberDto.getPwd());
                isCorrect.set(LoginFlag.INCORRECT_PASSWORD);
            }
        }

        if (isCorrect.get().equals(LoginFlag.SUCCESS)) {
            loginAttemptCheckerAgent.loginSucceeded(id);
            log.info("로그인 성공 처리 완료: {}", id);
            addRefreshTokenCookie(response);
            addAccessTokenCookie(response);
            return ResponseEntity.ok()
                    .body(ApiResponseFactory.createResponse("로그인 성공", memberDto));
        } else if (isCorrect.get().equals(LoginFlag.INCORRECT_ID)) {
            loginAttemptCheckerAgent.loginFailed(id);
            log.info("로그인 실패 - 존재하지 않는 아이디: {}", id);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ApiResponseFactory.createErrorResponse("존재하지 않는 아이디입니다."));
        } else if (isCorrect.get().equals(LoginFlag.INCORRECT_PASSWORD)) {
            loginAttemptCheckerAgent.loginFailed(id);
            log.info("로그인 실패 - 비밀번호 불일치: {}", id);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ApiResponseFactory.createErrorResponse("비밀번호가 틀렸습니다."));
        } else {
            loginAttemptCheckerAgent.loginFailed(id);
            log.info("로그인 실패: {}", id);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ApiResponseFactory.createErrorResponse("로그인 실패"));
        }
    }

    /**
     *         /*
     *         * HTTP/1.1 200 OK
     *             Content-Type: application/json;charset=UTF-8
     *             {
     *                 "token_type":"bearer",
     *                 "access_token":"${ACCESS_TOKEN}",
     *                 "expires_in":43199,
     *                 "refresh_token":"${REFRESH_TOKEN}",
     *                 "refresh_token_expires_in":5184000,
     *                 "scope":"account_email profile"
     *             }
     *             1. 토큰얻기 서비스 사용(사용자가 카카오로그인인증을 마쳤다는거임)
     *             2. 얻은 토큰으로 멤버정보얻기
     *             3. 얻은 정보로 처음이면 회원가입시키기, 있으면 로그인
     */
    @RequestMapping(value = "kakaoLogin", method = RequestMethod.GET)
    public Mono<ResponseEntity<ApiResponse<String>>> kakaoLogin(@RequestParam(value = "code") String authorizeCode,
                                                                HttpServletResponse response) throws Exception {
        log.info("카카오 로그인 요청: code={}", authorizeCode);

        return kakaoService.getAccessToken(authorizeCode)
                .publishOn(Schedulers.boundedElastic())
                .map(result -> {
                    log.info("액세스 토큰 받음: result={}", result);
                    JSONObject resultJsonObject = new JSONObject(result);
                    System.out.println("resultJsonObject");
                    System.out.println(resultJsonObject);
                    System.out.println(resultJsonObject.get("access_token"));
                    String accessToken = resultJsonObject.get("access_token").toString();///////////////////////////////////////
                    AtomicReference<String> memberId = new AtomicReference<>("");

                    try {
                        kakaoService.getMemberInformation(accessToken)
                                .doOnSubscribe(subscription -> log.info("회원 정보 요청 구독 시작"))
                                .doOnNext(res -> log.info("회원 정보 응답 수신: {}", res))
                                .doOnError(error -> log.info("회원 정보 요청 오류: {}", error.getMessage()))
                                .log()
                                .map(res -> {
                                    log.info("회원 정보 처리 중: res={}", res);
                                    KakaoResponseDto kakaoResponseDto = null;
                                    try {
                                        kakaoResponseDto = new ObjectMapper().readValue(res, KakaoResponseDto.class);
                                        System.out.println("kakaoResponse");
                                        System.out.println(kakaoResponseDto.toString());
                                    } catch (JsonProcessingException e) {
                                        log.error("JSON 처리 오류: {}", e.getMessage(), e);
                                        throw new RuntimeException(e);
                                    }

                                    String nickname = kakaoResponseDto.getKakaoAccount().getProfile().getNickname();
                                    String email = kakaoResponseDto.getKakaoAccount().getEmail();
                                    System.out.println("email ::: " + email);
                                    memberId.set(email);
                                    AddMemberDto newMember = new AddMemberDto();
                                    newMember.setMemberId(email);
                                    newMember.setNickname(nickname);
                                    newMember.setPwd("socialLogin");
                                    newMember.setPhoneNumber(RandomData.getRandomPhoneNumber());
//                                    try {
//                                        log.info("회원 조회 시도: memberId={}", id);
//                                        log.info("userService :: {}", memberService.findMemberById(id));
//                                    } catch (Exception e) {
//                                        log.error("회원 조회 중 오류: {}", e.getMessage(), e);
//                                        throw new RuntimeException(e);
//                                    }
                                    log.info("회원 정보 있는지 검증하고 없으면 추가 시도: id={}", email);
                                    Optional.ofNullable(memberService.findMemberById(email)).ifPresentOrElse(member -> {
                                            },
                                            () -> {//없으면...
                                                log.info("회원 정보 추가: Id={}", email);
                                                memberService.addMember(newMember);
                                            });
                                    return res;
                                }).block();
                    }catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                    String Url = "http://127.0.0.1:3000/member/kakaoNav?loginWay="+ "kakao";
                    System.out.println(Url);

                    addRefreshTokenCookie(response); // RefreshToken

                    Cookie memberIdCookie = new Cookie("Member-ID", memberId.get());
                    memberIdCookie.setPath("/");
                    memberIdCookie.setMaxAge(60 * 60 * 24 * 7);
                    response.addCookie(memberIdCookie);

                    addAccessTokenCookie(response); // AccessToken

                    return ResponseEntity.status(HttpStatus.PERMANENT_REDIRECT)
                            .header(HttpHeaders.LOCATION, Url)
                            .build();
                });
    }

    @PostMapping("/googleLogin")
    public ResponseEntity<?> googleLogin(@RequestBody googleLoginRequestDto dto,HttpServletResponse response) {
            log.info("구글 로그인 요청: {}", dto);

            AtomicReference<MemberDto> memberDtoAtomicReference = new AtomicReference<>();

        Optional.ofNullable(memberService.findMemberById(dto.getMemberId(), dto.getMemberId())).ifPresentOrElse(memberDto -> {
            log.info("회원 정보: {}", memberDto);
            memberDtoAtomicReference.set(memberDto);
        }, () -> {
            AddMemberDto addMemberDto = new AddMemberDto();
            addMemberDto.setMemberId(dto.getMemberId());
            addMemberDto.setNickname(dto.getNickname());
            addMemberDto.setPwd("socialLogin");
            addMemberDto.setPhoneNumber(RandomData.getRandomPhoneNumber());
            memberService.addMember(addMemberDto);

            MemberDto memberDto = memberService.findMemberById(dto.getMemberId(), dto.getMemberId());
            memberDtoAtomicReference.set(memberDto);

        });


        addRefreshTokenCookie(response);
        addAccessTokenCookie(response);

        return ResponseEntity.ok(ApiResponseFactory.createResponse("로그인 성공", memberDtoAtomicReference.get()));
    }
    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(@RequestBody LoginRequestDto dto) {
        // 토큰을 블랙리스트에 추가
//        tokenBlacklistService.addToken(token);
//
//        // SecurityContext에서 인증 정보 제거
//        SecurityContextHolder.clearContext();
        log.info("logout" + dto);

        return ResponseEntity.ok(ApiResponseFactory.createResponse("로그아웃 성공", null));
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
        MemberDto dto = memberService.findMemberById(fromId,memberId);
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
    private void addRefreshTokenCookie(HttpServletResponse response) {
        //RequestContext.getRefreshToken() 리프레시 토큰 생성하는 함수 호출
        System.out.println("RequestContext.getRefreshToken() :: "+ RequestContext.getRefreshToken());
        String encodedToken = URLEncoder.encode(RequestContext.getRefreshToken(), StandardCharsets.UTF_8);

        Cookie refreshTokenCookie = new Cookie("Refresh-Token", encodedToken);
        System.out.println("encodedToken :: "+ encodedToken);
        refreshTokenCookie.setHttpOnly(true);
//            refreshToken.setSecure(true); Https에서만 사용
        refreshTokenCookie.setPath("/");
        refreshTokenCookie.setMaxAge(60 * 60 * 24 * 7);//7일
        response.addCookie(refreshTokenCookie);
    }
    private void addAccessTokenCookie(HttpServletResponse response) {
        //RequestContext.getAuthorization() 액세스 토큰 생성하는 함수 호출
        System.out.println("RequestContext.getAuthorization() :: "+ RequestContext.getAuthorization());
        String encodedToken = URLEncoder.encode(RequestContext.getAuthorization(), StandardCharsets.UTF_8);
        Cookie accessTokenCookie = new Cookie("Authorization", encodedToken);
        System.out.println("encodedToken :: "+ encodedToken);
        accessTokenCookie.setHttpOnly(true);
//            refreshToken.setSecure(true); Https에서만 사용
        accessTokenCookie.setPath("/");
        accessTokenCookie.setMaxAge(60 * 60 * 24 * 7);//7일
        response.addCookie(accessTokenCookie);
    }
}
