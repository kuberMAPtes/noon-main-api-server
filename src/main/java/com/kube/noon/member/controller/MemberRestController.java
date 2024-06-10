package com.kube.noon.member.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kube.noon.common.binder.DtoEntityBinder;
import com.kube.noon.member.dto.RequestDto.LoginRequestDto;
import com.kube.noon.member.dto.RequestDto.MemberRelationshipSearchCriteriaRequestDto;
import com.kube.noon.member.dto.RequestDto.MemberSearchCriteriaRequestDto;
import com.kube.noon.member.dto.kakao.KakaoResponse;
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
        System.out.println("생성자 :: " + this.getClass());
        this.kakaoService = kakaoService;
        this.memberService = memberService;
        this.loginAttemptCheckerAgent = loginAttemptCheckerAgent;
    }


    @GetMapping("/sendAuthentificationNumber")
    public ResponseEntity<ApiResponse<Void>> sendAuthentificationNumber(@RequestParam String phoneNumber) {
        authService.sendAuthentificationNumber(phoneNumber);
        return ResponseEntity.ok(ApiResponseFactory.createResponse(phoneNumber + "로 인증 번호가 전송되었습니다.", null));
    }

    @GetMapping("/confirmAuthentificationNumber")
    public ResponseEntity<ApiResponse<Void>> confirmAuthentificationNumber(@RequestParam String phoneNumber, @RequestParam String authNumber) {
        boolean isConfirmed = authService.confirmAuthenticationNumber(phoneNumber, authNumber);
        if (isConfirmed) {
            return ResponseEntity.ok(ApiResponseFactory.createResponse("인증이 확인되었습니다.", null));
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ApiResponseFactory.createErrorResponse("잘못된 인증 번호입니다."));
        }
    }

    @GetMapping("/checkMemberId")
    public ResponseEntity<ApiResponse<String>> checkMemberId(@RequestParam String memberId) {
        memberService.checkMemberId(memberId);
        memberService.checkBadWord(memberId);
        return ResponseEntity.ok(ApiResponseFactory.createResponse("회원 ID를 사용할 수 있습니다.", null));
    }

    @GetMapping("/checkNickname")
    public ResponseEntity<ApiResponse<String>> checkNickname(@RequestParam String nickname) {
        memberService.checkNickname(nickname);
        memberService.checkBadWord(nickname);
        return ResponseEntity.ok(ApiResponseFactory.createResponse("닉네임을 사용할 수 있습니다.", null));
    }

    @GetMapping("/checkPhoneNumber")
    public ResponseEntity<ApiResponse<String>> checkPhoneNumber(@RequestParam String phoneNumber) {
        memberService.checkPhoneNumber(phoneNumber);
        return ResponseEntity.ok(ApiResponseFactory.createResponse("전화번호를 사용할 수 있습니다.", null));
    }

    @GetMapping("/checkPassword")
    public ResponseEntity<ApiResponse<String>> checkPassword(@RequestParam String memberId, @RequestParam String password) {
        memberService.checkPassword(memberId, password);
        return ResponseEntity.ok(ApiResponseFactory.createResponse("패스워드를 사용할 수 있습니다.", null));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<String>> login(@RequestBody LoginRequestDto dto) {
        String id = dto.getMemberId();
        AtomicReference<LoginFlag> isCorrect = new AtomicReference<>(LoginFlag.FAILURE);
        MemberDto memberDto = memberService.findMemberById(id, id);

        if (memberDto == null) {
            isCorrect.set(LoginFlag.INCORRECT_ID);
        } else {
            if (memberDto.getPwd().equals(dto.getPwd())) {
                isCorrect.set(LoginFlag.SUCCESS);
            } else {
                isCorrect.set(LoginFlag.INCORRECT_PASSWORD);
            }
        }

        if (isCorrect.get().equals(LoginFlag.SUCCESS)) {
            loginAttemptCheckerAgent.loginSucceeded(id);
            return ResponseEntity.ok()
                    .header("Authorization", RequestContext.getAuthorization())
                    .body(ApiResponseFactory.createResponse("로그인 성공", null));
        } else if (isCorrect.get().equals(LoginFlag.INCORRECT_ID)) {
            loginAttemptCheckerAgent.loginFailed(id);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ApiResponseFactory.createErrorResponse("존재하지 않는 아이디입니다."));
        } else if (isCorrect.get().equals(LoginFlag.INCORRECT_PASSWORD)) {
            loginAttemptCheckerAgent.loginFailed(id);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ApiResponseFactory.createErrorResponse("비밀번호가 틀렸습니다."));
        } else {
            loginAttemptCheckerAgent.loginFailed(id);
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
    public Mono<ResponseEntity<ApiResponse<String>>> kakaoLogin(@RequestParam(value = "code") String authorize_code) throws Exception {
        System.out.println("code : " + authorize_code);
        return kakaoService.getAccessToken(authorize_code)
                .publishOn(Schedulers.boundedElastic())
                //여기서 받은건 body를  Mono<String>으로 변환한걸 받고 Mono<ResponseEntity<?>>로 변환
                .map(result -> {
                    //액세스토큰 받은거로 kakaoService.getMemberInformation
                    System.out.println("result : " + result);
                    JSONObject ResultJsonObject = new JSONObject(result);
                    AtomicReference<String> memberId= new AtomicReference<>("");

                    try {
                        kakaoService.getMemberInformation(ResultJsonObject.get("access_token")+"")
                                .doOnSubscribe(subscription -> System.out.println("Request subscribed"))
                                .doOnNext(response -> System.out.println("Received response: " + response))
                                .doOnError(error -> System.out.println("Error occurred: " + error.getMessage()))
                                .log()
                                .map(response->{
                                    System.out.println("response : " + response);
                                    KakaoResponse kakaoResponse = null;
                                    try {
                                        kakaoResponse = new ObjectMapper().readValue(response, KakaoResponse.class);
                                    } catch (JsonProcessingException e) {
                                        throw new RuntimeException(e);
                                    }
                                    String nickname = kakaoResponse.getKakao_account().getProfile().getNickname();
                                    String id = kakaoResponse.getId();
                                    memberId.set(id);
                                    AddMemberDto newMember = new AddMemberDto();
                                    newMember.setMemberId(id);
                                    newMember.setNickname(nickname);
                                    newMember.setPwd("socialLogin");
                                    newMember.setPhoneNumber(RandomData.getRandomPhoneNumber());//카카오에서
                                    try {
                                        System.out.println("userService :: " + memberService.findMemberById(id));
                                    } catch (Exception e) {
                                        throw new RuntimeException(e);
                                    }
                                    try {
                                        //디비에 있으면 넘어가고 없으면 넣는다.
                                        Optional.ofNullable(memberService.findMemberById(id)).ifPresentOrElse((user1)->{},()-> {
                                            try {
                                                System.out.println("디비에 넣겠다.");
                                                memberService.addMember(newMember);
                                            } catch (Exception e) {
                                                throw new RuntimeException(e);
                                            }
                                        });

                                    } catch (Exception e) {
                                        throw new RuntimeException(e);
                                    }
                                    return response;
                                }).block();
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
//                    return ResponseEntity.status(HttpStatus.OK).body(result);
                    return ResponseEntity.status(HttpStatus.PERMANENT_REDIRECT)
                            .header(HttpHeaders.LOCATION, "http://127.0.0.1:3000/?access_token="+ ResultJsonObject.get("access_token")+"&memberId="+ memberId,
                                    "Authorization",RequestContext.getAuthorization())
                            .build();
                });//end of map
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
    public ResponseEntity<ApiResponse<Void>> updatePassword(@RequestBody UpdatePasswordDto requestDto) {
        memberService.updatePassword(requestDto);
        return ResponseEntity.ok(ApiResponseFactory.createResponse("비밀번호 변경 성공", null));
    }

    @PostMapping("/updatePhoneNumber")
    public ResponseEntity<ApiResponse<Void>> updatePhoneNumber(@RequestBody UpdatePhoneNumberDto requestDto) {
        memberService.updatePhoneNumber(requestDto);
        return ResponseEntity.ok(ApiResponseFactory.createResponse("전화번호 변경 성공", null));
    }

    @PostMapping("/updateProfilePhoto")
    public ResponseEntity<ApiResponse<Void>> updateProfilePhoto(@RequestBody UpdateMemberProfilePhotoUrlDto requestDto) {
        memberService.updateMemberProfilePhotoUrl(requestDto);
        return ResponseEntity.ok(ApiResponseFactory.createResponse("프로필 사진 변경 성공", null));
    }

    @PostMapping("/updateProfileIntro")
    public ResponseEntity<ApiResponse<Void>> updateProfileIntro(@RequestBody UpdateMemberProfileIntroDto requestDto) {
        memberService.updateMemberProfileIntro(requestDto);
        return ResponseEntity.ok(ApiResponseFactory.createResponse("프로필 소개 변경 성공", null));
    }

    @PostMapping("/updateDajungScore")
    public ResponseEntity<ApiResponse<Void>> updateDajungScore(@RequestBody UpdateMemberDajungScoreDto requestDto) {
        memberService.updateDajungScore(requestDto);
        return ResponseEntity.ok(ApiResponseFactory.createResponse("다정점수 변경 성공", null));
    }

    /**
     * 관리자가 사용
     */
    @GetMapping("/getMember/{fromId}/{memberId}/")
    public ResponseEntity<ApiResponse<MemberDto>> getMember(@PathVariable String fromId, @PathVariable String memberId) {
        MemberDto dto = memberService.findMemberById(fromId,memberId);
        return ResponseEntity.ok(ApiResponseFactory.createResponse("회원 조회 성공", dto));
    }

    @GetMapping("/getMemberProfile/{fromId}/{toId}")
    public ResponseEntity<ApiResponse<MemberProfileDto>> getMemberProfile(@PathVariable String fromId, @PathVariable String toId) {
        MemberProfileDto dto = memberService.findMemberProfileById(fromId, toId);
        return ResponseEntity.ok(ApiResponseFactory.createResponse("회원 프로필 조회 성공", dto));
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
        return ResponseEntity.ok(ApiResponseFactory.createResponse("회원이 성공적으로 삭제되었습니다.", null));
    }

    @PostMapping("/addMemberRelationship")
    public ResponseEntity<ApiResponse<String>> addMemberRelationship(@RequestBody AddMemberRelationshipDto dto) {
        memberService.addMemberRelationship(dto);
        return ResponseEntity.ok(
                ApiResponseFactory.createResponse("관계가 성공적으로 추가되었습니다.", null)
        );
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
        return ResponseEntity.ok(ApiResponseFactory.createResponse(message, null));
    }

    //이게
    private String validateJwtToken(String token) {
        // JWT 토큰 검증 로직 구현
        // 유효한 토큰이면 사용자 ID를 반환하고, 그렇지 않으면 예외를 던집니다.
        return "member_100"; // 예시로 사용자 ID 반환
    }


}
