package com.kube.noon.member.validator;

import com.kube.noon.common.binder.DtoEntityBinder;
import com.kube.noon.common.validator.IllegalServiceCallException;
import com.kube.noon.common.validator.ValidationChain;
import com.kube.noon.member.domain.Member;
import com.kube.noon.member.exception.MemberSecurityBreachException;
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
import java.util.Base64;
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
    private static final String[] IMAGE_EXTENSIONS = { "png", "jpg", "jpeg", "gif", "webp" };

    private final WebClient webClient;
    private final ValidationChain validationChain;
    private final MemberRepository memberRepository;

    public MemberScanner(ValidationChain validationChain, MemberRepository memberRepository) {
        this.validationChain = validationChain;
        this.memberRepository = memberRepository;
        this.webClient = WebClient.builder().build();
    }

    //공통함수
    public <T>void scanIsDataNull(T data){
        if (data == null) {
            throw new IllegalServiceCallException("받은 데이터가 없습니다.");
        }

        if (data instanceof String && ((String) data).trim().isEmpty()) {
            throw new IllegalServiceCallException("받은 데이터가 없습니다.");
        }
    }
    /**
     * 내부 속성 null 검사, 내부 속성 형식 검사
     * @param dto
     * @param <T>
     */
    public <T> void scanDtoField(T dto) {
        log.info("scanDtoField 실행");
        validationChain.validate(dto);
    }

    /**
     * scanIsMemberSignedOff, scanIsMemberAlreadyExist, scanIsMemberNotExist
     *
     * @param dto
     * @param <T>
     */
    public <T> void scanIsMemberSignedOff(T dto){
        log.info("scanIsMemberSignedOff 실행");

        Member member = (Member) DtoEntityBinder.INSTANCE.toEntity(dto);
        if(member.getSignedOff()){
            throw new MemberSecurityBreachException("탈퇴한 회원입니다.");
        }

    }

    /**
     * 회원이 탈퇴하지 않았는지 검사
     * @param memberId
     */
    public void scanIsMemberSignedOff(String memberId) {
        memberRepository.findMemberById(memberId)
                .ifPresent(member -> {
                    if (member.getSignedOff()) {
                        throw new MemberSecurityBreachException("탈퇴한 회원입니다.");
                    }
                });
    }

    /**
     *회원이 존재하는지 && 탈퇴하지 않았는지 검사
     */
    public void scanIsMemberExist(String memberId) {
        memberRepository.findMemberById(memberId).ifPresentOrElse(
                (member)->{
                    scanIsMemberSignedOff(member);
                },
                ()->{
                    throw new IllegalServiceCallException("존재하지 않는 회원입니다.");
                }
        );
    }
    public void scanIsMemberAlreadyExist(String memberId) {
        if (memberRepository.findMemberById(memberId).isPresent()) {
            throw new IllegalServiceCallException("이미 존재하는 회원 아이디입니다.");
        }
    }
    public void scanIsSameMember(String fromId, String toId) {
        if (fromId.equals(toId)) {
            throw new IllegalServiceCallException("자기 자신과의 관계는 설정할 수 없습니다.");
        }
    }
    public boolean scanIsMemberRelationshipAlreadyExist(String fromId, String toId) {
        return memberRepository.findMemberRelationship(fromId, toId).isPresent();
    }
    public void scanMemberIdPattern(String memberId){
        if (!MEMBER_ID_PATTERN.matcher(memberId).matches()) {
            throw new IllegalServiceCallException("회원 아이디는 6자 이상 16자 이하여야 합니다.");
        }
    }
    public void scanNicknameIsAlreadyExist(String nickname) {
        if (memberRepository.findMemberByNickname(nickname).isPresent()) {
            throw new IllegalServiceCallException("이미 존재하는 닉네임입니다.");
        }
    }
    public void scanNicknamePattern(String nickname) {
        if (!NICKNAME_PATTERN.matcher(nickname).matches()) {
            throw new IllegalServiceCallException("형식에 맞지 않는 닉네임입니다. 닉네임은 2자 이상 20자 이하여야 합니다.");
        }
    }
    public void scanPhoneNumberIsAlreadyExist(String phoneNumber) {
        if (memberRepository.findMemberByPhoneNumber(phoneNumber).isPresent()) {
            throw new IllegalServiceCallException("이미 존재하는 전화번호입니다.");
        }
    }
    public void scanPhoneNumberPattern(String phoneNumber) {
        if (!PHONE_NUMBER_PATTERN.matcher(phoneNumber).matches()) {
            throw new IllegalServiceCallException("전화번호 형식이 올바르지 않습니다. 올바른 형식 예: 010-XXXX-XXXX");
        }
    }
    public void scanPasswordPattern(String password) {
        if (!PASSWORD_PATTERN.matcher(password).matches()) {
            throw new IllegalServiceCallException("비밀번호는 8자 이상 16자 이하여야 합니다.");
        }
    }
    public void scanDajungScorePattern(int dajungScore) {
        if (dajungScore <= 0 || dajungScore >= 100) {
            throw new IllegalServiceCallException("다정 점수는 0 이상 100이하 이어야 합니다.");
        }
    }
    public void scanImageFileName(String fileName) {
        String fileExtension = getFileExtension(fileName).toLowerCase();
        boolean isValid = false;
        for (String extension : IMAGE_EXTENSIONS) {
            if (fileExtension.equals(extension)) {
                isValid = true;
                break;
            }
        }
        if (!isValid) {
            throw new IllegalServiceCallException("유효한 이미지 파일 이름이 아닙니다.");
        }
    }
    public String getFileExtension(String fileName) {
        if (fileName == null || fileName.lastIndexOf('.') == -1) {
            return "";
        }
        return fileName.substring(fileName.lastIndexOf('.') + 1);
    }
    public void scanImageUrl(String urlString) {
        try {
            byte[] body = webClient.get()
                    .uri(urlString)
                    .retrieve()
                    .onStatus(status -> !status.is2xxSuccessful(), clientResponse -> {
                        int statusCode = clientResponse.statusCode().value();
                        return Mono.error(new IllegalServiceCallException("URL이 유효하지 않습니다. 응답 코드: " + statusCode));
                    })
                    .bodyToMono(byte[].class)
                    .block();

            String contentType = webClient.get()
                    .uri(urlString)
                    .retrieve()
                    .onStatus(status -> !status.is2xxSuccessful(), clientResponse -> {
                        int statusCode = clientResponse.statusCode().value();
                        return Mono.error(new IllegalServiceCallException("URL이 유효하지 않습니다. 응답 코드: " + statusCode));
                    })
                    .toEntity(byte[].class)
                    .mapNotNull(entity -> entity.getHeaders().getContentType())
                    .map(MediaType::toString)
                    .block();

            if (contentType == null || !contentType.startsWith("image/")) {
                throw new IllegalServiceCallException("URL이 이미지 파일을 가리키지 않습니다.");
            }

            if (body == null || body.length == 0) {
                throw new IllegalServiceCallException("URL이 유효한 이미지 파일을 가리키지 않습니다.");
            }

            try (InputStream inputStream = new ByteArrayInputStream(body)) {
                BufferedImage image = ImageIO.read(inputStream);
                if (image == null) {
                    throw new IllegalServiceCallException("URL이 유효한 이미지 파일을 가리키지 않습니다.");
                }
            }

        } catch (Exception e) {
            throw new IllegalServiceCallException("URL이 유효한 이미지 파일을 가리키지 않습니다.", e);
        }
    }
    public void scanBase64Image(String base64String) {
        try {
            String[] parts = base64String.split(",");
            String metadata = parts[0];
            String base64Data = parts[1];
            String mimeType = metadata.split(":")[1].split(";")[0];

            if (!mimeType.startsWith("image/")) {
                throw new IllegalServiceCallException("지원하지 않는 Base64 데이터 형식입니다.");
            }

            byte[] dataBytes = Base64.getDecoder().decode(base64Data);
            try (InputStream inputStream = new ByteArrayInputStream(dataBytes)) {
                BufferedImage image = ImageIO.read(inputStream);
                if (image == null) {
                    throw new IllegalServiceCallException("Base64 데이터가 유효한 이미지 파일을 가리키지 않습니다.");
                }
            }
        } catch (Exception e) {
            throw new IllegalServiceCallException("Base64 데이터가 유효하지 않습니다.", e);
        }
    }
    public void scanProfilePhotoUrlPattern(String profilePhotoString) {

        // URL 패턴 검증
        if (URL_PATTERN.matcher(profilePhotoString).matches()) {
            scanImageUrl(profilePhotoString);
        }
        // Base64 패턴 검증
        else if (BASE64_PATTERN.matcher(profilePhotoString).matches()) {
            scanBase64Image(profilePhotoString);
        }
        // 파일 이름 패턴 검증
        else {
            scanImageFileName(profilePhotoString);
        }
    }



}
