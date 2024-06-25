package com.kube.noon.member.controller;

import com.kube.noon.common.binder.DtoEntityBinder;
import com.kube.noon.common.messagesender.ApickApiAgent;
import com.kube.noon.common.messagesender.ApickApiAgentImpl;
import com.kube.noon.common.security.SecurityConstants;
import com.kube.noon.common.security.TokenPair;
import com.kube.noon.common.security.authentication.authtoken.TokenType;
import com.kube.noon.common.security.support.BearerTokenSupport;
import com.kube.noon.common.security.support.InvalidRefreshTokenException;
import com.kube.noon.common.security.support.KakaoTokenSupport;
import com.kube.noon.member.dto.RequestDto.*;
import com.kube.noon.member.dto.ResponseDto.SearchMemberResponseDto;
import com.kube.noon.member.dto.auth.googleLoginRequestDto;
import com.kube.noon.member.dto.member.*;
import com.kube.noon.member.dto.memberRelationship.*;
import com.kube.noon.member.dto.search.MemberRelationshipSearchCriteriaDto;
import com.kube.noon.member.dto.search.MemberSearchCriteriaDto;
import com.kube.noon.member.dto.util.RandomData;
import com.kube.noon.member.enums.LoginFlag;
import com.kube.noon.member.enums.RelationshipType;
import com.kube.noon.member.repository.impl.AuthRepositoryImpl;
import com.kube.noon.member.service.AuthService;
import com.kube.noon.member.service.KakaoService;
import com.kube.noon.member.service.LoginAttemptCheckerAgent;
import com.kube.noon.member.service.MemberService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.*;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.View;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import static com.kube.noon.member.controller.AESUtil.*;

/**
 * 음? 왜 바로 ok만 내보내지? -> GlobalExceptionHandler에서 검증하고 있음
 * 음? 왜 memberService 에서 그냥 ok만 내보내지? ->memberValidator, memberValidationRule,memberScanner 에서 검증하고 있음
 * <p>
 * 잘못된 설계가 하나 있음 RequestDto로 쓰이는 Dto중에 FromId가 존재하는 것들이 있음. RequestDto에서 FromId를 꺼내서 쓰면 절대 안됨 비었음.
 */
@Tag(name = "Member", description = "회원 관련 API")
@Slf4j
@RestController
@RequestMapping("/member")
public class MemberRestController {

    private final MemberService memberService;

    private final LoginAttemptCheckerAgent loginAttemptCheckerAgent;

    private final KakaoService kakaoService;

    private final AuthService authService;
    private final List<BearerTokenSupport> tokenSupport;
    private final View error;
    private final AuthRepositoryImpl authRepositoryImpl;

    private final String clientServerHost;

    private final String clientServerPort;

    @Value("${client-server-domain}")
    private String clientServerDomain;

    // Constructor
    public MemberRestController(@Qualifier("memberServiceImpl") MemberService memberService,
                                @Qualifier("loginAttemptCheckerAgent") LoginAttemptCheckerAgent loginAttemptCheckerAgent,
                                KakaoService kakaoService,
                                AuthService authService,
                                List<BearerTokenSupport> tokenSupport, View error, AuthRepositoryImpl authRepositoryImpl,
                                @Value("${client.server.host}") String clientServerHost,
                                @Value("${client.server.port}") String clientServerPort) {
        this.authService = authService;
        log.info("생성자 :: " + this.getClass());
        this.kakaoService = kakaoService;
        this.memberService = memberService;
        this.loginAttemptCheckerAgent = loginAttemptCheckerAgent;
        this.tokenSupport = tokenSupport;
        this.error = error;
        this.authRepositoryImpl = authRepositoryImpl;
        this.clientServerHost = clientServerHost;
        this.clientServerPort = clientServerPort;
    }

    @Operation(summary = "문자날리기", description = "문자날립니다")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "업데이트 성공"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "유효하지 않은 파라미터"
            )
    })
    @GetMapping("/sendAuthentificationNumber")
    public ResponseEntity<ApiResponse<Boolean>> sendAuthentificationNumber(@RequestParam String phoneNumber) {
        log.info("sendAuthentificationNumber :: " + phoneNumber);
        Boolean isSended = authService.sendAuthentificationNumber(phoneNumber);
//        Boolean isSended = true;
        if(isSended) {
            return ResponseEntity.ok(ApiResponseFactory.createResponse(phoneNumber + "로 인증 번호가 전송되었습니다.", true));
        }else{
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponseFactory.createErrorResponse("인증 번호 전송 실패",false));
        }
    }


    @Operation(summary = "인증 번호 확인", description = "인증 번호를 확인합니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "인증이 확인되었습니다."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "잘못된 인증 번호입니다.")
    })
    @GetMapping("/confirmAuthentificationNumber")
    public ResponseEntity<ApiResponse<Boolean>> confirmAuthentificationNumber(@RequestParam String phoneNumber, @RequestParam String authNumber) {

        log.info("confirmAuthentificationNumber :: " + phoneNumber + " " + authNumber);
        
        Map<String,Object> mapResult = authService.confirmAuthenticationNumber(phoneNumber, authNumber);

        if ((boolean) mapResult.get("result")){//성공한거
            return ResponseEntity.ok(ApiResponseFactory.createResponse("인증이 확인되었습니다.", true));
        }else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ApiResponseFactory.createErrorResponse((String) mapResult.get("message"), false));
        }

    }


    @Operation(summary = "회원 ID 확인", description = "회원 ID의 유효성을 확인합니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "회원 ID를 사용할 수 있습니다."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "유효하지 않은 회원 ID")
    })
    @GetMapping("/checkMemberId")
    public ResponseEntity<ApiResponse<Boolean>> checkMemberId(@RequestParam String memberId) {
        log.info("checkMemberId :: " + memberId);
        //회원가입전 체크하는 것
        memberService.checkMemberId(memberId);
        memberService.checkBadWord(memberId);
        return ResponseEntity.ok(ApiResponseFactory.createResponse("회원 ID를 사용할 수 있습니다.", true));
    }
    @GetMapping("/checkMemberIdExisted")
    public ResponseEntity<ApiResponse<Boolean>> checkMemberIdExisted(@RequestParam String memberId){
        log.info("checkMemberIdExisted :: " + memberId);
        memberService.checkMemberIdExisted(memberId);
        return ResponseEntity.ok(ApiResponseFactory.createResponse("회원 ID를 사용할 수 있습니다.", true));
    }
    @GetMapping("/checkPhoneNumberAndMemberId")
    public ResponseEntity<ApiResponse<Boolean>> checkPhoneNumberAndMemberId(@RequestParam String phoneNumber, @RequestParam String memberId){
        log.info("checkPhoneNumberAndMemberId :: " + phoneNumber + " " + memberId);
        memberService.checkPhoneNumberAndMemberId(phoneNumber, memberId);
        return ResponseEntity.ok(ApiResponseFactory.createResponse("입력한 전화번호가 회원정보에 있는 전화번호와 일치합니다.", true));
    }

    @Operation(summary = "닉네임 확인", description = "닉네임의 유효성을 확인합니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "닉네임을 사용할 수 있습니다."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "유효하지 않은 닉네임")
    })
    @GetMapping("/checkNickname")
    public ResponseEntity<ApiResponse<Boolean>> checkNickname(@RequestParam String nickname) {
        log.info("checkNickname :: " + nickname);
        memberService.checkNickname(nickname);
        memberService.checkBadWord(nickname);
        return ResponseEntity.ok(ApiResponseFactory.createResponse("닉네임을 사용할 수 있습니다.", true));
    }


    @Operation(summary = "전화번호 확인", description = "전화번호의 유효성을 확인합니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "전화번호를 사용할 수 있습니다."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "유효하지 않은 전화번호")
    })
    @GetMapping("/checkPhoneNumber")
    public ResponseEntity<ApiResponse<Boolean>> checkPhoneNumber(@RequestParam String phoneNumber) {
        log.info("checkPhoneNumber :: " + phoneNumber);
        memberService.checkPhoneNumber(phoneNumber);
        return ResponseEntity.ok(ApiResponseFactory.createResponse("전화번호를 사용할 수 있습니다.", true));
    }


    @Operation(summary = "비밀번호 확인", description = "회원 ID와 비밀번호의 유효성을 확인합니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "패스워드를 사용할 수 있습니다."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "유효하지 않은 패스워드")
    })
    @GetMapping("/checkPassword")
    public ResponseEntity<ApiResponse<Boolean>> checkPassword(@RequestParam String memberId, @RequestParam String password) {
        log.info("checkPassword :: " + memberId + " " + password);
        memberService.checkPassword(memberId, password);
        return ResponseEntity.ok(ApiResponseFactory.createResponse("패스워드를 사용할 수 있습니다.", true));
    }

    @GetMapping("/getLoginMember")
    public ResponseEntity<ApiResponse<?>> getLoginMember(
            @CookieValue("token") String accessToken,
            @CookieValue("token_type") TokenType tokenType
    ) {
        for (BearerTokenSupport ts : tokenSupport) {
            if (ts.supports(tokenType)) {
                String memberId = ts.extractMemberId(accessToken);
                return ResponseEntity.ok(ApiResponseFactory.createResponse(
                        "현재 로그인한 유저 정보",
                        this.memberService.findMemberById(memberId, memberId))
                );
            }
        }
        return ResponseEntity.badRequest().body(ApiResponseFactory.createResponse("유효하지 않은 토큰 타입", String.format("""
                {
                    "token_type": %s
                }
                """, tokenType)));
    }

    @Operation(summary = "토큰 갱신", description = "리프레시 토큰을 사용하여 액세스 토큰을 갱신합니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "토큰 갱신 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "유효하지 않은 리프레시 토큰")
    })
    @GetMapping("/refresh")
    public ResponseEntity<ApiResponse<Boolean>> refreshToken(
            @CookieValue(value = "token_type", required = false) String tokenTypeStr,
            @CookieValue(value = "refresh_token", required = false) String refreshToken,
            HttpServletResponse response
    ) {
        TokenType tokenType;
        try {
            tokenType = TokenType.valueOf(tokenTypeStr);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(ApiResponseFactory.createErrorResponse("Invalid refresh token",false), HttpStatus.FORBIDDEN);
        }

        for (BearerTokenSupport ts : this.tokenSupport) {
            if (ts.supports(tokenType)) {
                try {
                    TokenPair tokenPair = ts.refreshToken(refreshToken);
                    addTokenToCookie(response, tokenPair, tokenType);
                    return new ResponseEntity<>(ApiResponseFactory.createResponse("Success", true), HttpStatus.OK);
                } catch (InvalidRefreshTokenException e) {
                    return new ResponseEntity<>(ApiResponseFactory.createErrorResponse("Invalid refresh token",false), HttpStatus.FORBIDDEN);
                }
            }
        }
        return new ResponseEntity<>(ApiResponseFactory.createErrorResponse("Invalid token type",false), HttpStatus.FORBIDDEN);
    }

    @Operation(summary = "로그인", description = "사용자가 아이디와 비밀번호를 통해 로그인합니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "로그인 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "로그인 실패")
    })
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<MemberDto>> login(@RequestBody LoginRequestDto dto, HttpServletResponse response) {
        log.info("로그인 요청: {}", dto);
        String memberId = dto.getMemberId();
        String errorMessage = "";

        if(loginAttemptCheckerAgent.isLoginLocked(memberId)){
            System.out.println("로그인 시도 횟수 초과 30초간 잠금상태입니다.");

            errorMessage = "로그인 시도 횟수 초과 30초간 잠금상태입니다.";

            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponseFactory.createErrorResponse(errorMessage, null));
        }

        memberService.checkLoginMemberIdPattern(memberId);
        memberService.checkPassword(memberId,dto.getPwd());
        MemberDto memberDto = memberService.findMemberById(memberId, memberId);

        LoginFlag loginFlag = LoginFlag.FAILURE;
        if (memberDto == null) {
            log.info("존재하지 않는 아이디: {}", memberId);
            loginFlag = LoginFlag.INCORRECT_ID;
        } else {
            if (memberDto.getPwd().equals(dto.getPwd())) {
                log.info("로그인 업무: {}", memberId);
                loginFlag = LoginFlag.SUCCESS;
            } else {
                log.info("비밀번호 불일치: {} {} 원래 아이디 비번 : {} {} ", memberId, dto.getPwd(), memberDto.getMemberId(), memberDto.getPwd());
                loginFlag = LoginFlag.INCORRECT_PASSWORD;
            }
        }

        switch (loginFlag) {
            case SUCCESS:
                this.loginAttemptCheckerAgent.loginSucceeded(memberId); // TODO: With Redis
                log.info("로그인 업무 처리 완료: {}", memberId);


                TokenPair tokenPair = this.tokenSupport.stream()
                        .filter((tokenSupport) -> tokenSupport.supports(TokenType.NATIVE_TOKEN))
                        .findAny()
                        .orElseThrow()
                        .generateToken(memberId);
                addTokenToCookie(response, tokenPair, TokenType.NATIVE_TOKEN);
                return ResponseEntity.ok()
                        .body(ApiResponseFactory.createResponse("로그인 업무", memberDto));
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

        this.loginAttemptCheckerAgent.loginFailed(memberId);
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(ApiResponseFactory.createErrorResponse(errorMessage, null));
    }

    private void addTokenToCookie(HttpServletResponse response, TokenPair tokenPair, TokenType tokenType) {
        response.addHeader("Set-Cookie", wrapWithCookie(SecurityConstants.ACCESS_TOKEN_COOKIE_KEY.get(), tokenPair.getAccessToken()));
        response.addHeader("Set-Cookie", wrapWithCookie(SecurityConstants.REFRESH_TOKEN_COOKIE_KEY.get(), tokenPair.getRefreshToken()));
        response.addHeader("Set-Cookie", wrapWithCookie(SecurityConstants.TOKEN_TYPE_COOKIE_KEY.get(), tokenType.name()));
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
    @Operation(summary = "카카오 로그인", description = "사용자가 카카오 로그인 인증을 마친 후 토큰을 통해 로그인합니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "302", description = "카카오 로그인 성공 및 리다이렉트"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "카카오 로그인 실패")
    })
    @RequestMapping(value = "kakaoLogin", method = RequestMethod.GET)
    public ResponseEntity<ApiResponse<String>> kakaoLogin(@RequestParam(value = "code") String authorizeCode,
                                                          HttpServletResponse response) throws Exception {
        log.info("카카오 로그인 요청: code={}", authorizeCode);

        TokenPair tokenPair = this.kakaoService.generateTokenPairAndAddMemberIfNotExists(authorizeCode);
        addTokenToCookie(response, tokenPair, TokenType.KAKAO_TOKEN);


        String memberId = "";
        for (BearerTokenSupport element : tokenSupport) {
            if (element.supports(TokenType.KAKAO_TOKEN)) {
                memberId = ((KakaoTokenSupport) element).getMemberInformation(tokenPair.getAccessToken()).getMemberId();
            }
        }

        if (memberId.isEmpty()) {
            memberId = memberService.findMemberById(memberId).get().getMemberId();
        }

//        List<String> encryptedMemberId = encryptAES(memberId);


//        Cookie cookie = new Cookie("IV", encryptedMemberId.get(0));
//        cookie.setPath("/");
//        cookie.setMaxAge(60 * 60 * 24 * 7);
//        cookie.setHttpOnly(false);
//
//        if(this.clientServerDomain.equals("localhost")) {
//            cookie.setSecure(false);
//        }else {
//            cookie.setSecure(true);
//
//        }
//
//        cookie.setDomain(this.clientServerDomain);
//        cookie.setAttribute("SameSite", "None"); // SameSite 설정
//
//        response.addCookie(cookie);
//
//        Cookie cookie2 = new Cookie("Member-ID", encryptedMemberId.get(1));
//        cookie2.setPath("/");
//        cookie2.setMaxAge(60 * 60 * 24 * 7);
//        cookie2.setHttpOnly(false);
//
//        if(this.clientServerDomain.equals("localhost")) {
//            cookie.setSecure(false);
//        }else {
//            cookie.setSecure(true);
//        }
//        System.out.println("도메인 : " + this.clientServerDomain);
//        cookie2.setDomain(this.clientServerDomain);
//        cookie2.setAttribute("SameSite", "None"); // SameSite 설정
//        response.addCookie(cookie2);
//        String secretId = encryptedMemberId.get(0);
//        String secretIv = encryptedMemberId.get(1);
        String redirectClientUrl;
        String trimmedHost = clientServerHost.trim();
        String trimmedPort = clientServerPort.trim();

        if(!clientServerPort.isEmpty()) {
            redirectClientUrl = trimmedHost + ":" + trimmedPort + "/member/kakaoNav/"+memberId+"?loginWay=" + "kakao";
        }else{//비었으면 Host만
            redirectClientUrl = trimmedHost + "/member/kakaoNav/"+memberId+"?loginWay=" + "kakao";
        }

        System.out.println("리다이렉트 합니다 " + redirectClientUrl);

        return ResponseEntity.status(HttpStatus.PERMANENT_REDIRECT)
                .header(HttpHeaders.LOCATION, redirectClientUrl)
                .build();
    }

    @Operation(summary = "구글 로그인", description = "사용자가 구글 로그인 인증을 마친 후 토큰을 통해 로그인합니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "구글 로그인 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "구글 로그인 실패")
    })
    @PostMapping("/googleLogin")
    public ResponseEntity<?> googleLogin(@RequestBody googleLoginRequestDto dto, HttpServletResponse response) {
        log.info("구글 로그인 요청: {}", dto);

        AtomicReference<MemberDto> memberDtoAtomicReference = new AtomicReference<>();

        Optional.ofNullable(memberService.findMemberById(dto.getMemberId(), dto.getMemberId())).ifPresentOrElse(memberDto -> {
            //존재하면 PASS, 존재하지 않으면 회원가입
            log.info("회원 정보: {}", memberDto);
            memberDtoAtomicReference.set(memberDto);
        }, () -> {
            AddMemberDto addMemberDto = new AddMemberDto();
            addMemberDto.setMemberId(dto.getMemberId());
            addMemberDto.setNickname(dto.getNickname());
            addMemberDto.setPwd("social_sign_up");
            //만약 존재하는 아이디라면 GlobalExceptionHandler에서 처리된다.
            //프론트엔드에서 info를 받았을 때 memberId가 있는지 보면 된다.
            addMemberDto.setSocialSignUp(true);
            memberService.addMember(addMemberDto);

            MemberDto memberDto = memberService.findMemberById(dto.getMemberId(), dto.getMemberId());
            memberDtoAtomicReference.set(memberDto);

        });

//        addTokenToCookie();

        return ResponseEntity.ok(ApiResponseFactory.createResponse("로그인 업무", memberDtoAtomicReference.get()));
    }

    @GetMapping("/searchMember")
    public ResponseEntity<ApiResponse<Page<SearchMemberResponseDto>>> searchMember(
            @RequestParam("requesterId") String requesterId,
            @RequestParam("searchKeyword") String searchKeyword,
            @RequestParam("page") int page
    ) {
        return new ResponseEntity<>(ApiResponseFactory.createResponse("OK", this.memberService.searchMemberByNickname(requesterId, searchKeyword, page)), HttpStatus.OK);
    }

    @Operation(summary = "로그아웃", description = "사용자가 로그아웃합니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "로그아웃 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "로그아웃 실패")
    })
    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Boolean>> logout(
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

        return ResponseEntity.ok(ApiResponseFactory.createResponse("로그아웃 업무", true));
    }

    private Cookie getDestructionCookie(String key) {
        Cookie cookie = new Cookie(key, null);
        cookie.setMaxAge(0);
        cookie.setDomain(this.clientServerDomain);
        cookie.setPath("/");
        return cookie;
    }

    @Operation(summary = "회원 가입", description = "새로운 사용자를 회원으로 등록합니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "회원가입 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "회원가입 실패")
    })
    @PostMapping("/addMember")
    public ResponseEntity<ApiResponse<Boolean>> addMember(@Valid @RequestBody AddMemberDto dto, BindingResult bindingResult) {
        log.info("addMember" + dto + " " + bindingResult);
        log.info("왜 로그가 안찍혀");
        if (bindingResult.hasErrors()) {
            log.info("여기는 머야");
            Map<String, String> errors = new HashMap<>();
            bindingResult.getFieldErrors().forEach(fieldError -> {
                errors.put(fieldError.getField(), fieldError.getDefaultMessage());
            });
            log.info(errors.toString());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponseFactory.createErrorResponse("회원가입 실패",false));
        }

        log.info("왜 로그가 안찍혀");
        memberService.addMember(dto);

        return ResponseEntity.ok(ApiResponseFactory.createResponse("회원가입 업무", true));
    }

    @Operation(summary = "비밀번호 변경", description = "사용자의 비밀번호를 변경합니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "비밀번호 변경 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "비밀번호 변경 실패")
    })
    @PostMapping("/updatePwd")
    public ResponseEntity<ApiResponse<Boolean>> updatePassword(@RequestBody UpdatePasswordDto requestDto) {
        log.info("updatePwd" + requestDto);
        memberService.updatePassword(requestDto);
        return ResponseEntity.ok(ApiResponseFactory.createResponse("비밀번호 변경 업무", true));
    }

    @Operation(summary = "전화번호 변경", description = "사용자의 전화번호를 변경합니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "전화번호 변경 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "전화번호 변경 실패")
    })
    @PostMapping("/updatePhoneNumber")
    public ResponseEntity<ApiResponse<Boolean>> updatePhoneNumber(@RequestBody UpdatePhoneNumberDto requestDto) {
        log.info("updatePhoneNumber" + requestDto);
        memberService.updatePhoneNumber(requestDto);
        return ResponseEntity.ok(ApiResponseFactory.createResponse("전화번호 변경 업무", true));
    }

    @Operation(summary = "프로필 사진 변경", description = "사용자의 프로필 사진을 변경합니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "프로필 사진 변경 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "프로필 사진 변경 실패")
    })
    @PostMapping("/updateProfilePhoto")
    public ResponseEntity<ApiResponse<Boolean>> updateProfilePhoto(@RequestBody UpdateMemberProfilePhotoUrlDto requestDto) {
        log.info("updateProfilePhoto" + requestDto);
        memberService.updateMemberProfilePhotoUrl(requestDto);
        return ResponseEntity.ok(ApiResponseFactory.createResponse("프로필 사진 변경 업무", true));
    }

    @Operation(summary = "프로필 소개 변경", description = "사용자의 프로필 소개를 변경합니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "프로필 소개 변경 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "프로필 소개 변경 실패")
    })
    @PostMapping("/updateProfileIntro")
    public ResponseEntity<ApiResponse<Boolean>> updateProfileIntro(@RequestBody UpdateMemberProfileIntroDto requestDto) {
        log.info("updateProfileIntro" + requestDto);
        memberService.updateMemberProfileIntro(requestDto);
        return ResponseEntity.ok(ApiResponseFactory.createResponse("프로필 소개 변경 업무", true));
    }

    @Operation(summary = "다정점수 변경", description = "사용자의 다정점수를 변경합니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "다정점수 변경 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "다정점수 변경 실패")
    })
    @PostMapping("/updateDajungScore")
    public ResponseEntity<ApiResponse<Boolean>> updateDajungScore(@RequestBody UpdateMemberDajungScoreDto requestDto) {
        log.info("updateDajungScore" + requestDto);
        memberService.updateDajungScore(requestDto);
        return ResponseEntity.ok(ApiResponseFactory.createResponse("다정점수 변경 업무", true));
    }

    @PostMapping("/updateMember")
    public ResponseEntity<ApiResponse<Boolean>> updateMember(@RequestBody UpdateMemberDto requestDto) {
        log.info("updateMember" + requestDto);
        memberService.updateMember(requestDto);
        return ResponseEntity.ok(ApiResponseFactory.createResponse("회원 정보 변경 업무", true));
    }

    /**
     * 관리자가 사용
     */
    @Operation(summary = "회원 조회", description = "관리자가 사용자를 조회합니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "회원 조회 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "회원 조회 실패")
    })
    @PostMapping("/getMember")
    public ResponseEntity<ApiResponse<MemberDto>> getMember(@RequestBody GetMemberRequestDto requestDto) {
        log.info("getMember :: " + requestDto);
        MemberDto dto = memberService.findMemberById(requestDto.getMemberId(), requestDto.getMemberId());
        System.out.println("회원조회업무 :: " + dto);
        return ResponseEntity.ok(ApiResponseFactory.createResponse("회원 조회 업무", dto));
    }
    @GetMapping("/getMemberIdByPhoneNumber")
    public ResponseEntity<ApiResponse<String>> getMemberByPhoneNumber(@RequestParam String phoneNumber){
        log.info("getMemberByPhoneNumber :: " + phoneNumber);
        MemberDto dto = memberService.findMemberByPhoneNumber(phoneNumber);
        System.out.println("회원조회업무 :: " + dto);
        return ResponseEntity.ok(ApiResponseFactory.createResponse("", dto.getMemberId()));
    }

    @Operation(summary = "회원 프로필 조회", description = "사용자의 프로필을 조회합니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "회원 프로필 조회 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "회원 프로필 조회 실패")
    })
    @PostMapping("/getMemberProfile")
    public ResponseEntity<ApiResponse<MemberProfileDto>> getMemberProfile(@RequestBody GetMemberProfileRequestDto requestDto) {
        log.info("getMemberProfile" + requestDto);
        MemberProfileDto dto = memberService.findMemberProfileById(requestDto.getFromId(), requestDto.getToId());
        System.out.println(dto.toString());
        return ResponseEntity.ok(ApiResponseFactory.createResponse("회원 프로필 조회 업무", dto));
    }

    @Operation(summary = "회원 목록 조회", description = "사용자 목록을 조회합니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "회원 목록 조회 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "회원 목록 조회 실패")
    })
    @PostMapping("/listMember")
    public ResponseEntity<List<MemberDto>> listMember(@RequestBody MemberSearchCriteriaRequestDto requestDto) {
        log.info("listMember" + requestDto);
        MemberSearchCriteriaDto memberSearchCriteriaDto = DtoEntityBinder.INSTANCE.toOtherDto(requestDto);
        Page<MemberDto> members = memberService.findMemberListByCriteria(requestDto.getMemberId(), memberSearchCriteriaDto, requestDto.getPageUnit(), requestDto.getPageSize());
        List<MemberDto> memberDtoList = members.stream().toList();
        return ResponseEntity.ok(memberDtoList);
    }

    @Operation(summary = "회원 삭제", description = "관리자가 사용자를 삭제합니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "회원 삭제 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "회원 삭제 실패")
    })
    @PostMapping("/deleteMember/{memberId}")
    public ResponseEntity<ApiResponse<Boolean>> deleteMember(@RequestBody String memberId) {
        log.info("deleteMember" + memberId);

        memberService.deleteMember(memberId);
        return ResponseEntity.ok(ApiResponseFactory.createResponse("회원이 성공적으로 삭제되었습니다.", true));
    }

    @Operation(summary = "회원 관계 추가", description = "사용자 간의 관계를 추가합니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "회원 관계 추가 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "회원 관계 추가 실패")
    })
    @PostMapping("/addMemberRelationship")
    public ResponseEntity<ApiResponse<Boolean>> addMemberRelationship(@RequestBody AddMemberRelationshipDto dto) {
        log.info("addMemberRelationship" + dto);
        memberService.addMemberRelationship(dto);
        return ResponseEntity.ok(
                ApiResponseFactory.createResponse("관계가 성공적으로 추가되었습니다.", true)
        );
    }

    @Operation(summary = "회원 관계 목록 조회", description = "사용자 간의 관계 목록을 조회합니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "회원 관계 목록 조회 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "회원 관계 목록 조회 실패")
    })
    @PostMapping("/getMemberRelationshipList")
    public ResponseEntity<ApiResponse<Map<String,Object>>> getMemberRelationshipList(@RequestBody MemberRelationshipSearchCriteriaRequestDto requestDto) {
        log.info("getMemberRelationshipList{}", requestDto);
        MemberRelationshipSearchCriteriaDto memberRelationshipSearchCriteriaDto = DtoEntityBinder.INSTANCE.toOtherDto(requestDto);

        FindMemberRelationshipListByCriteriaResponseDto responseDto = memberService.findMemberRelationshipListByCriteria(requestDto.getFromId(), memberRelationshipSearchCriteriaDto, requestDto.getPage(), requestDto.getSize());
        log.info("responseDto :: {}", responseDto.toString());

        List<MemberRelationshipSimpleDto> relationshipSimpleDtoList = responseDto.getMemberRelationshipDtoPage().getContent()
                .stream()
                .map(memberRelationshipDto -> DtoEntityBinder.INSTANCE.toDto(memberRelationshipDto, MemberRelationshipSimpleDto.class))
                .toList();

        Map<String,Object> map = new HashMap<>();
        map.put("dtoList",relationshipSimpleDtoList);
        map.put("totalFollowingCount",responseDto.getMemberRelationshipCountDto().getFollowingCount());
        map.put("totalFollowerCount",responseDto.getMemberRelationshipCountDto().getFollowerCount());
        map.put("totalBlockingCount",responseDto.getMemberRelationshipCountDto().getBlockingCount());//내가 차단한 회원
        map.put("totalBlockerCount",responseDto.getMemberRelationshipCountDto().getBlockerCount());//얘는 관리자만 사용
        return ResponseEntity.ok(ApiResponseFactory.createResponse("관계를 성공적으로 조회",map));
    }

    @GetMapping("/getFollowRelationship")
    public ResponseEntity<ApiResponse<Map<String,Object>>> getFollowRelationship(@RequestParam String fromId, String toId) {
        log.info("getMemberRelationship :: " + fromId + " " + toId);
        MemberRelationshipSimpleDto dto1 = memberService.findMemberRelationshipSimple(fromId, toId);

        //getRelationshipType이 FOLLOW가 아니면 null을 리턴
        if(dto1!=null) {
            if (dto1.getRelationshipType() != RelationshipType.FOLLOW) {
                dto1 = null;
            }else if( Boolean.FALSE.equals(dto1.getActivated())){
                dto1 = null;
            }
        }

        MemberRelationshipSimpleDto dto2 = memberService.findMemberRelationshipSimple(toId, fromId);

        if(dto2!=null) {
            if (dto2.getRelationshipType() != RelationshipType.FOLLOW) {
                dto2 = null;
            }else if( Boolean.FALSE.equals(dto2.getActivated())){
                dto2 = null;
            }
        }

        Map<String, Object> map = new HashMap<>();
        map.put("from", dto1);
        map.put("to", dto2);

        return ResponseEntity.ok(ApiResponseFactory.createResponse("관계를 성공적으로 조회", map));
    }

    @Operation(summary = "회원 관계 삭제", description = "사용자 간의 관계를 삭제합니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "회원 관계 삭제 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "회원 관계 삭제 실패")
    })
    @PostMapping("/deleteMemberRelationship")
    public ResponseEntity<ApiResponse<Boolean>> deleteMemberRelationship(
            @RequestBody DeleteMemberRelationshipDto requestDto,
            @CookieValue(value = "token", required = false) String accessToken,
            @CookieValue(value = "token_type", required = false) String tokenTypeStr,
            @CookieValue(value = "refresh_token", required = false) String refreshToken,
            HttpServletResponse response
    ) {
//        log.info("deleteMemberRelationship" + requestDto);
//        // JWT 토큰 검증
//        if (accessToken == null) {
//            return new ResponseEntity<>(ApiResponseFactory.createErrorResponse("access token is null",false), HttpStatus.FORBIDDEN);
//        }
//
//        TokenType tokenType;
//        try {
//            tokenType = TokenType.valueOf(tokenTypeStr);
//        } catch (IllegalArgumentException e) {
//            return new ResponseEntity<>(ApiResponseFactory.createErrorResponse("Not supported token type=" + tokenTypeStr,false), HttpStatus.FORBIDDEN);
//        }
//
//        try {
//            for (BearerTokenSupport ts : this.tokenSupport) {
//                if (ts.supports(tokenType)) {
//                    String fromId = ts.extractMemberId(accessToken);
//                    requestDto.setFromId(fromId);
//                    memberService.deleteMemberRelationship(requestDto);
//                    String message = requestDto.getRelationshipType() == RelationshipType.FOLLOW ? "팔로우가 해제되었습니다." : "차단이 해제되었습니다.";
//                    return ResponseEntity.ok(ApiResponseFactory.createResponse(message, true));
//                }
//            }
//            return new ResponseEntity<>(ApiResponseFactory.createErrorResponse("Not supported token type=" + tokenTypeStr,false), HttpStatus.FORBIDDEN);
//        } catch (Exception e) {
//            log.warn("Exception in processing token", e);
//            return new ResponseEntity<>(ApiResponseFactory.createErrorResponse("Invalid access token",false), HttpStatus.FORBIDDEN);
//        }
        memberService.deleteMemberRelationship(requestDto);
        String message = requestDto.getRelationshipType() == RelationshipType.FOLLOW ? "팔로우가 해제되었습니다." : "차단이 해제되었습니다.";
        return ResponseEntity.ok(ApiResponseFactory.createResponse(message, true));
    }

    @Operation(summary = "회원 프로필 사진 조회", description = "회원의 프로필 사진을 조회합니다.")
    @GetMapping("/getProfilePhoto")
    public ResponseEntity<byte[]> getProfilePhoto(@Parameter(description = "회원 ID") @RequestParam String memberId) {

        return memberService.findMemberProfilePhoto(memberId);
    }

    @Operation(summary = "회원 프로필 사진 추가", description = "회원의 프로필 사진을 추가합니다.")
    @PostMapping("/updateProfilePhoto/{memberId}")
    public ResponseEntity<String> updateProfilePhoto(
            @RequestParam("multipartFile") MultipartFile multipartFile,
            @Parameter(description = "회원 ID") @PathVariable("memberId") String memberId) {
        try {
            String profilePhotoUrl = memberService.updateMemberProfilePhotoUrl(memberId, multipartFile);
            return new ResponseEntity<>(profilePhotoUrl, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    private String wrapWithCookie(String cookieName, String value) {
        log.info("client-server-domain={}", this.clientServerDomain);

        ResponseCookie built = ResponseCookie.from(cookieName, value)
                .httpOnly(true)
                .path("/")
                .secure(true)
                .domain(this.clientServerDomain)
                .maxAge(7 * 24 * 60 * 60)
                .sameSite("None")
                .build();
        log.info("response cookie={}", built);
        return built.toString();
    }
}
