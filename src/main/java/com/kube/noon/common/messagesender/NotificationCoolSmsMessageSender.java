package com.kube.noon.common.messagesender;

import com.kube.noon.member.domain.Member;
import com.kube.noon.notification.domain.NotificationType;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Hex;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.UUID;

@Slf4j
@Component
@Profile("prod")
public class NotificationCoolSmsMessageSender implements NotificationMessageSender {
    private static final String URL = "https://api.coolsms.co.kr/messages/v4/send-many/detail";
    private static final String KOREA_COUNTRY_CODE = "82";

    private final String accessKey;
    private final String secretKey;
    private final String fromPhoneNumber;
    private final RestTemplate restTemplate;

    public NotificationCoolSmsMessageSender(@Value("${cool-sms.access-key}") String accessKey,
                                            @Value("${cool-sms.secret-key}") String secretKey,
                                            @Value("${cool-sms.from-phone-number}") String fromPhoneNumber) {
        this.accessKey = accessKey;
        this.secretKey = secretKey;
        this.fromPhoneNumber = fromPhoneNumber;
        this.restTemplate = new RestTemplate();
    }

    @Override
    public void send(Member receiver, String text, NotificationType notificationType) {
        send(receiver, generateText(text, notificationType));
    }

    private String generateText(String text, NotificationType notificationType) {
        String header;
        switch (notificationType) {
            case COMMENT -> header = "[댓글 알림]";
            case REPORT -> header = "[신고 알림]";
            case LIKE -> header = "[좋아요 알림]";
            default -> header = "";
        }
        return header + "\n" + text;
    }

    @Override
    public void send(Member receiver, String text) {
        String salt = UUID.randomUUID().toString().replaceAll("-", "");
        String formattedDate = ZonedDateTime.now(ZoneId.of("Asia/Seoul")).toString().split("\\[")[0];
        try {
            String signature = getSignature(salt, formattedDate);
            String authorizationHeader = "HMAC-SHA256 apiKey="
                    + this.accessKey
                    + ", date="
                    + formattedDate
                    + ", salt="
                    + salt
                    + ", signature="
                    + signature;
            System.out.println(authorizationHeader);
            sendRequest(authorizationHeader, receiver.getPhoneNumber(), text);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        } catch (InvalidKeyException e) {
            log.error("적합하지 않은 Key", e);
            throw new RuntimeException(e);
        }
    }

    private String getSignature(String salt, String date) throws NoSuchAlgorithmException, InvalidKeyException {
        Mac sha256Hmac = Mac.getInstance("HmacSHA256");
        SecretKeySpec secretKeySpec =
                new SecretKeySpec(this.secretKey.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
        sha256Hmac.init(secretKeySpec);
        return String.valueOf(Hex.encodeHex(sha256Hmac.doFinal((date + salt).getBytes(StandardCharsets.UTF_8))));
    }

    private void sendRequest(String authHeader,
                             String receiverPhoneNumber,
                             String text) {
        JSONArray messages = new JSONArray();
        JSONObject message = new JSONObject();
        message.put("to", receiverPhoneNumber.replaceAll("-", ""));
        message.put("from", this.fromPhoneNumber);
        message.put("text", text);
        message.put("type", "SMS");
        message.put("country", KOREA_COUNTRY_CODE);
        messages.put(message);
        JSONObject body = new JSONObject();
        body.put("messages", messages);
        System.out.println(body.toString());
        RequestEntity<String> requestEntity = RequestEntity.post(URL)
                .headers((h) -> {
                    h.add("Authorization", authHeader);
                    h.add("Content-Type", "application/json");
                })
                .body(body.toString());

        ResponseEntity<String> responseEntity = this.restTemplate.exchange(requestEntity, String.class);
        log.trace(responseEntity.getBody());
    }
}
