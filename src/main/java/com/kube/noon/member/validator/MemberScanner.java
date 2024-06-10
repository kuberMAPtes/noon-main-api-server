package com.kube.noon.member.validator;

import com.kube.noon.common.binder.DtoEntityBinder;
import com.kube.noon.common.validator.IllegalServiceCallException;
import com.kube.noon.common.validator.Problems;
import com.kube.noon.common.validator.ValidationChain;
import com.kube.noon.member.domain.Member;
import com.kube.noon.member.dto.memberRelationship.AddMemberRelationshipDto;
import com.kube.noon.member.enums.Role;
import com.kube.noon.member.repository.MemberRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Map;
import java.util.regex.Pattern;

@Component
@Slf4j
public class MemberScanner {

    private static final Pattern PHONE_NUMBER_PATTERN = Pattern.compile("^01(?:0|1|[6-9])-(?:\\d{3}|\\d{4})-\\d{4}$");
    private static final Pattern NICKNAME_PATTERN = Pattern.compile("^[a-zA-Z0-9가-힣_ ]{2,20}$");
    private static final Pattern MEMBER_ID_PATTERN = Pattern.compile("^(?=.*[a-zA-Z])[a-zA-Z0-9_]{6,16}$");
    private static final Pattern PASSWORD_PATTERN = Pattern.compile("^(?=.*[a-zA-Z])(?=.*[0-9])[a-zA-Z0-9!@#\\$%\\^&\\*_]{8,16}$");
    private static final Pattern URL_PATTERN = Pattern.compile("^(https?|ftp)://[^\s/$.?#].[^\s]*$", Pattern.CASE_INSENSITIVE);
    private static final Pattern BASE64_PATTERN = Pattern.compile("^data:image/(png|jpg|jpeg|gif|webp);base64,[a-zA-Z0-9+/]+={0,2}$");
    private static final Pattern IMAGE_FILE_PATTERN = Pattern.compile("^.*\\.(png|jpg|jpeg|gif|webp)$", Pattern.CASE_INSENSITIVE);


    private final WebClient webClient;
    private final ValidationChain validationChain;
    private final MemberRepository memberRepository;

    public MemberScanner(ValidationChain validationChain, MemberRepository memberRepository) {
        this.validationChain = validationChain;
        this.memberRepository = memberRepository;
        this.webClient = WebClient.builder().build();
    }

    //공통함수
    public <T> void imoDataNotNull(T data){
        if (data == null) {
            throw new IllegalServiceCallException("데이터가 널입니다.", new Problems(Map.of("data", data)));
        }

        if (data instanceof String && ((String) data).trim().isEmpty()) {
            throw new IllegalServiceCallException("데이터가 널입니다.", new Problems(Map.of("data", data)));
        }
    }

    public <T, U> void imoTwoDataNotNullSimul(T data1, U data2) {
        boolean isData1Null = (data1 == null) || (data1 instanceof String && ((String) data1).trim().isEmpty());
        boolean isData2Null = (data2 == null) || (data2 instanceof String && ((String) data2).trim().isEmpty());

        if (isData1Null && isData2Null) {
            throw new IllegalServiceCallException("두 데이터가 모두 널입니다.", new Problems(Map.of("data1", data1, "data2", data2)));
        }
    }

    //3개
    public <T, U, V> void imoThreeDataNotNullSimul(T data1, U data2, V data3) {
        boolean isData1Null = (data1 == null) || (data1 instanceof String && ((String) data1).trim().isEmpty());
        boolean isData2Null = (data2 == null) || (data2 instanceof String && ((String) data2).trim().isEmpty());
        boolean isData3Null = (data3 == null) || (data3 instanceof String && ((String) data3).trim().isEmpty());

        if (isData1Null && isData2Null && isData3Null) {
            throw new IllegalServiceCallException("세 데이터가 모두 널입니다.", new Problems(Map.of("data1", data1, "data2", data2, "data3", data3)));
        }
    }

    /**
     * 내부 속성 null 검사, 내부 속성 형식 검사
     * imo = In my opinion 내 생각에
     * O = Ok,Yes
     * is는 생략
     * imoDataNotNull : 데이터가 낫널이다 = 낫널일 것이다 = 낫널이여야함 = 데이터가 널이면 에러
     *
     * @param dto
     * @param <T>
     */
    public <T> void imoDtoFieldO(T dto) {
        log.info("imoDtoFieldO 실행");
        validationChain.validate(dto);
    }

    /**
     * imoMemberNotSignedOff, imoMemberNotAlreadyExist, scanIsMemberNotExist
     *
     * @param dto
     * @param <T>
     */
    public <T> void imoMemberNotSignedOff(T dto){
        log.info("imoMemberNotSignedOff 실행");

        Member member = (Member) DtoEntityBinder.INSTANCE.toEntity(dto);
        if(member.getSignedOff()){
            throw new IllegalServiceCallException("탈퇴한 회원입니다.", new Problems(Map.of("memberId", member.getMemberId())));
        }
    }

    public void imoMemberNotSignedOff(AddMemberRelationshipDto dto) {
        memberRepository.findMemberById(dto.getFromId())
                .ifPresent(member -> {
                    if (member.getSignedOff()) {
                        throw new IllegalServiceCallException("탈퇴한 회원입니다.", new Problems(Map.of("memberId", dto.getFromId())));
                    }
                });
        memberRepository.findMemberById(dto.getToId())
                .ifPresent(member -> {
                    if (member.getSignedOff()) {
                        throw new IllegalServiceCallException("탈퇴한 회원입니다.", new Problems(Map.of("memberId", dto.getToId())));
                    }
                });
    }

    /**
     * 회원이 탈퇴하지 않았는지 검사
     * @param memberId
     */
    public void imoMemberNotSignedOff(String memberId) {
        memberRepository.findMemberById(memberId)
                .ifPresent(member -> {
                    if (member.getSignedOff()) {
                        throw new IllegalServiceCallException("탈퇴한 회원입니다.", new Problems(Map.of("memberId", memberId)));
                    }
                });
    }


    /**
     * 회원생각에 존재하는 회원인지 검사
     *회원관계 데이터가 있는지 + 탈퇴하지 않았는지 검사
     * 회원 관계 데이터가 있음 + 탈퇴안함-> 에러
     * 회원 관계 데이터가 있음 + 탈퇴함 -> 에러X
     * 회원 관계 데이터가 없음 -> 에러X
     */
    //존재하면 OK 존재하지 않으면 에러
    public void imoMemberRelationshipExist(String fromId, String toId) {
        if (memberRepository.findMemberRelationship(fromId, toId).isEmpty()) {
            throw new IllegalServiceCallException("존재하지 않는 회원 관계입니다.",new Problems(Map.of("fromId",fromId,"toId",toId)));
        }
    }
    // 존재하지 않으면 OK, 존재하면 에러
    public void imoMemberRelationshipNotExist(String fromId, String toId) {
        if (memberRepository.findMemberRelationship(fromId, toId).isPresent()) {
            throw new IllegalServiceCallException("이미 존재하는 회원 관계입니다.", new Problems(Map.of("fromId", fromId, "toId", toId)));
        }
    }

    public void imoMemberIdExist(String memberId) {
        if (memberRepository.findMemberById(memberId).isEmpty()) {
            throw new IllegalServiceCallException("존재하지 않는 회원 아이디입니다.", new Problems(Map.of("memberId", memberId)));
        }
    }

    public void imoMemberNicknameExist(String nickname) {
        if (memberRepository.findMemberByNickname(nickname).isEmpty()) {
            throw new IllegalServiceCallException("존재하지 않는 닉네임입니다.", new Problems(Map.of("nickname", nickname)));
        }
    }
    public void imoMemberPhoneNumberExist(String phoneNumber) {
        if (memberRepository.findMemberByPhoneNumber(phoneNumber).isEmpty()) {
            throw new IllegalServiceCallException("존재하지 않는 전화번호입니다.", new Problems(Map.of("phoneNumber", phoneNumber)));
        }
    }

    public void imoMemberNicknameNotExist(String nickname) {
        if (memberRepository.findMemberByNickname(nickname).isPresent()) {
            throw new IllegalServiceCallException("이미 존재하는 닉네임입니다.", new Problems(Map.of("nickname", nickname)));
        }
    }

    public void imoMemberPhoneNumberNotExist(String phoneNumber) {
        if (memberRepository.findMemberByPhoneNumber(phoneNumber).isPresent()) {
            throw new IllegalServiceCallException("이미 존재하는 전화번호입니다.", new Problems(Map.of("phoneNumber", phoneNumber)));
        }
    }

    public void imoMemberIsAdmin(String memberId){
        if(!memberRepository.findMemberById(memberId).get().getMemberRole().equals(Role.ADMIN)){
            throw new IllegalServiceCallException("관리자가 아닙니다.", new Problems(Map.of("memberId", memberId)));
        }
    }

    public void imoMemberIdNotExist(String memberId) {
        if (memberRepository.findMemberById(memberId).isPresent()) {
            throw new IllegalServiceCallException("이미 존재하는 회원 아이디입니다.", new Problems(Map.of("memberId", memberId)));
        }
    }

    public void imoMemberNotSame(String fromId, String toId) {
        if (fromId.equals(toId)) {
            throw new IllegalServiceCallException("자기 자신과의 관계는 설정할 수 없습니다.", new Problems(Map.of("fromId", fromId, "toId", toId)));
        }
    }

    public void imoNicknameNotAlreadyExist(String nickname) {
        if (memberRepository.findMemberByNickname(nickname).isPresent()) {
            throw new IllegalServiceCallException("이미 존재하는 닉네임입니다.", new Problems(Map.of("nickname", nickname)));
        }
    }

    public void imoPhoneNumberNotAlreadyExist(String phoneNumber) {
        if (memberRepository.findMemberByPhoneNumber(phoneNumber).isPresent()) {
            throw new IllegalServiceCallException("이미 존재하는 전화번호입니다.", new Problems(Map.of("phoneNumber", phoneNumber)));
        }
    }

    public void imoMemberIdPatternO(String memberId){
        if (!MEMBER_ID_PATTERN.matcher(memberId).matches()) {
            throw new IllegalServiceCallException("회원 아이디는 6자 이상 16자 이하여야 합니다.", new Problems(Map.of("memberId", memberId)));
        }
    }

    public void imoNicknamePatternO(String nickname) {
        if (!NICKNAME_PATTERN.matcher(nickname).matches()) {
            throw new IllegalServiceCallException("형식에 맞지 않는 닉네임입니다. 닉네임은 2자 이상 20자 이하여야 합니다.", new Problems(Map.of("nickname", nickname)));
        }
    }

    public void imoPhoneNumberPatternO(String phoneNumber) {
        if (!PHONE_NUMBER_PATTERN.matcher(phoneNumber).matches()) {
            throw new IllegalServiceCallException("전화번호 형식이 올바르지 않습니다. 올바른 형식 예: 010-XXXX-XXXX", new Problems(Map.of("phoneNumber", phoneNumber)));
        }
    }

    public void imoPwdPatternO(String password) {
        if (!PASSWORD_PATTERN.matcher(password).matches()) {
            throw new IllegalServiceCallException("비밀번호는 8자 이상 16자 이하여야 합니다.", new Problems(Map.of("password", password)));
        }
    }

    public void imoDajungScorePatternO(int dajungScore) {
        if (dajungScore < 0 || dajungScore > 100) {
            throw new IllegalServiceCallException("다정 점수는 0 이상 100이하 이어야 합니다.", new Problems(Map.of("dajungScore", dajungScore)));
        }
    }

    public void imoProfileIntroPatternO(String profileIntro) {
        if (profileIntro.length() > 150) {
            throw new IllegalServiceCallException("프로필 소개는 150자 이하여야 합니다.", new Problems(Map.of("profileIntro", profileIntro)));
        }
    }

    public void imoUnlockTimePatternO(LocalDateTime unlockTime) {
        LocalDateTime exceptionalTime = LocalDateTime.of(1, 1, 1, 1, 1, 1);
        if (!unlockTime.equals(exceptionalTime) && unlockTime.isBefore(LocalDateTime.now())) {
            throw new IllegalServiceCallException("잠금 해제 시간은 현재 시간 이후이여야 합니다. 아니면 디폴트 시간 이어야 합니다.", new Problems(Map.of("unlockTime", unlockTime)));
        }
    }

    public String getFileExtension(String fileName) {
        if (fileName == null || fileName.lastIndexOf('.') == -1) {
            return "";
        }
        return fileName.substring(fileName.lastIndexOf('.') + 1);
    }

    public void imoImageUrlO(String urlString) {
        try {
            byte[] body = webClient.get()
                    .uri(urlString)
                    .retrieve()
                    .onStatus(status -> !status.is2xxSuccessful(), clientResponse -> {
                        int statusCode = clientResponse.statusCode().value();
                        return Mono.error(new IllegalServiceCallException("URL이 유효하지 않습니다. 응답 코드: " + statusCode, new Problems(Map.of("urlString", urlString))));
                    })
                    .bodyToMono(byte[].class)
                    .block();

            String contentType = webClient.get()
                    .uri(urlString)
                    .retrieve()
                    .onStatus(status -> !status.is2xxSuccessful(), clientResponse -> {
                        int statusCode = clientResponse.statusCode().value();
                        return Mono.error(new IllegalServiceCallException("URL이 유효하지 않습니다. 응답 코드: " + statusCode, new Problems(Map.of("urlString", urlString))));
                    })
                    .toEntity(byte[].class)
                    .mapNotNull(entity -> entity.getHeaders().getContentType())
                    .map(MediaType::toString)
                    .block();

            if (contentType == null || !contentType.startsWith("image/")) {
                throw new IllegalServiceCallException("URL이 이미지 파일을 가리키지 않습니다.", new Problems(Map.of("urlString", urlString)));
            }

            if (body == null || body.length == 0) {
                throw new IllegalServiceCallException("URL이 유효한 이미지 파일을 가리키지 않습니다.", new Problems(Map.of("urlString", urlString)));
            }

            try (InputStream inputStream = new ByteArrayInputStream(body)) {
                BufferedImage image = ImageIO.read(inputStream);
                if (image == null) {
                    throw new IllegalServiceCallException("URL이 유효한 이미지 파일을 가리키지 않습니다.", new Problems(Map.of("urlString", urlString)));
                }
            }

        } catch (Exception e) {
            throw new IllegalServiceCallException("URL이 유효한 이미지 파일을 가리키지 않습니다.", e, new Problems(Map.of("urlString", urlString)));
        }
    }

    public void imoBase64ImageO(String base64String) {
        try {
            log.info("imoBase64ImageO 메서드 시작");

            String[] parts = base64String.split(",");
            if (parts.length != 2) {
                log.info("잘못된 Base64 형식: " + base64String);
                throw new IllegalServiceCallException("Base64 데이터 형식이 올바르지 않습니다.", new Problems(Map.of("base64String", base64String)));
            }
            log.info("Base64 문자열을 성공적으로 분할했습니다");

            String metadata = parts[0];
            String base64Data = parts[1];
            log.info("메타데이터: " + metadata);
            log.info("Base64 데이터: " + base64Data.substring(0, Math.min(30, base64Data.length())) + "..."); // Base64 데이터 일부만 출력

            String mimeType = metadata.split(":")[1].split(";")[0];
            log.info("Mime 타입 추출: " + mimeType);

            if (!mimeType.startsWith("image/")) {
                log.info("지원하지 않는 mime 타입: " + mimeType);
                throw new IllegalServiceCallException("지원하지 않는 Base64 데이터 형식입니다.", new Problems(Map.of("base64String", base64String)));
            }
            log.info("Mime 타입이 지원됩니다");

            byte[] dataBytes = Base64.getDecoder().decode(base64Data);
            log.info("Base64 데이터를 성공적으로 디코딩했습니다, 데이터 길이: " + dataBytes.length);

            try (InputStream inputStream = new ByteArrayInputStream(dataBytes)) {
                BufferedImage image = ImageIO.read(inputStream);
                if (image == null) {
                    log.info("ImageIO.read가 null을 반환했습니다, 유효하지 않은 이미지 데이터");
                    throw new IllegalServiceCallException("Base64 데이터가 유효한 이미지 파일을 가리키지 않습니다.", new Problems(Map.of("base64String", base64String)));
                }
                log.info("Base64 데이터에서 이미지를 성공적으로 읽었습니다");
            }
        } catch (IllegalServiceCallException e) {
            log.info("IllegalServiceCallException 발생: " + e.getMessage());
            throw e;
        } catch (Exception e) {
            log.info("Exception 발생: " + e.getMessage());
            throw new IllegalServiceCallException("Base64 데이터가 유효하지 않습니다.", e, new Problems(Map.of("base64String", base64String)));
        } finally {
            log.info("imoBase64ImageO 메서드 실행 종료");
        }
    }

    public void imoProfilePhotoUrlPatternO(String profilePhotoString) {

        // null이면 통과
        if(profilePhotoString == null || profilePhotoString.isEmpty()){
            return;
        }
        // URL 패턴 검증
        if (URL_PATTERN.matcher(profilePhotoString).matches()) {
            imoImageUrlO(profilePhotoString);
            return;
        }
        // Base64 패턴 검증
        if (BASE64_PATTERN.matcher(profilePhotoString).matches()) {
            imoBase64ImageO(profilePhotoString);
            return;
        }
        // 파일 이름 패턴 검증
        if (IMAGE_FILE_PATTERN.matcher(profilePhotoString).matches()) {
            throw new IllegalServiceCallException("유효한 이미지 파일 이름이 아닙니다.", new Problems(Map.of("profilePhotoString", profilePhotoString)));
        }
    }




}
