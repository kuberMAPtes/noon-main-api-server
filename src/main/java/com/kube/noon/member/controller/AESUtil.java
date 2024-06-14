package com.kube.noon.member.controller;

import com.google.common.base.Charsets;
import com.google.common.hash.Hashing;
import jakarta.annotation.PostConstruct;
import jakarta.xml.bind.DatatypeConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.AlgorithmParameters;
import java.util.Base64;
import java.util.ArrayList;
import java.util.List;

@Component
public class AESUtil {

    private static final String ALGORITHM = "AES";


    private static String STATIC_KEY; // 16바이트 고정 키

    @Value("${crypto.secret.key}")
    private String staticKeyValue;

    @PostConstruct
    private void init() {
        STATIC_KEY = staticKeyValue;
    }

    private static SecretKeySpec getSecretKey() {

        return new SecretKeySpec(Base64.getEncoder().encode(STATIC_KEY.getBytes()), ALGORITHM);
    }

    public static List<String> encryptAES(String data) {
        try {
            //시크릿을 UTF8 인코딩으로 바이트 배열로 변환
            byte[] secretKeys = STATIC_KEY.getBytes(StandardCharsets.UTF_8);
            // AES/CBC/PKCS5Padding 알고리즘을 사용하는 Cipher 객체 생성
            final SecretKeySpec secret = new SecretKeySpec(secretKeys, "AES");
            // 암호화 모드로 초기화
            final Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, secret);
            // 암호화에 사용되는 초기화 벡터 생성
            final AlgorithmParameters params = cipher.getParameters();

            // 초기화 벡터로 암호화된 데이터를 생성
            final byte[] iv = params.getParameterSpec(IvParameterSpec.class).getIV();//초기화 벡터.암호 알고리즘에 사용.
            final byte[] cipherText = cipher.doFinal(data.getBytes(Charsets.UTF_8));//실제 암호화 작업

            // 암호화된 데이터와 초기화 벡터를 Base64로 인코딩
            String ivBase64 = Base64.getEncoder().encodeToString(iv);
            String cipherTextBase64 = Base64.getEncoder().encodeToString(cipherText);

            List<String> result = new ArrayList<>();
            result.add(ivBase64);
            result.add(cipherTextBase64);

            return result;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static List<String> decryptAES(List<String> base64Data) {
        try {
            String ivBase64 = base64Data.get(0);
            String cipherTextBase64 = base64Data.get(1);

            // Base64로 인코딩된 초기화 벡터와 암호화된 데이터를 디코딩
            byte[] iv = Base64.getDecoder().decode(ivBase64);
            byte[] cipherText = Base64.getDecoder().decode(cipherTextBase64);

            //시크릿을 UTF8 인코딩으로 바이트 배열로 변환
            byte[] secretKeys = STATIC_KEY.getBytes(StandardCharsets.UTF_8);

            // AES/CBC/PKCS5Padding 알고리즘을 사용하는 Cipher 객체 생성
            final Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(secretKeys, "AES"), new IvParameterSpec(iv));

            // 복호화된 데이터를 생성
            String decryptedData = new String(cipher.doFinal(cipherText), Charsets.UTF_8);

            List<String> result = new ArrayList<>();
            result.add(ivBase64);
            result.add(decryptedData);

            return result;
        } catch (BadPaddingException e) {
            throw new IllegalArgumentException("Secret key is invalid");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) {
        String originalText = "Hello, World!";
        String secretKey = "g5j4ti8e3n1q9m6i";

        // AESUtil을 사용하여 데이터 암호화
        List<String> encryptedData = AESUtil.encryptAES(originalText);
        System.out.println("Encrypted Data: " + encryptedData);

        // 암호화된 데이터와 IV를 전달하여 복호화
        List<String> decryptedData = AESUtil.decryptAES(encryptedData);
        System.out.println("Decrypted Data: " + decryptedData);

        System.out.println("STATIC_KEY" +  STATIC_KEY);
        //스태틱키가 UTF8 문자열이 아니라면 encodedKey와 decodedKey가 다를 수 있다.
        //문자열을 영국영어 호주영어라고 생각하고, 바이트를 한국어라고 생각해보자.
        //문자열은 그냥 봐서 이게 호주영어인지 영국영어인지 알 수가 없다.
        //UTF8은 예를 들어 호주영어<->한 번역기이다. 호주영어를 ISO로 번역하면
        //ISO는 예를 들어 영국영어<->한 번역기이다.
        String encodedKey = new String(STATIC_KEY.getBytes(StandardCharsets.UTF_8), StandardCharsets.UTF_8);
        System.out.println("Encoded Key: " + encodedKey);
        String decodedKey = new String(encodedKey.getBytes(StandardCharsets.UTF_8), StandardCharsets.UTF_8);
        System.out.println("Decoded Key: " + decodedKey);
        String encodedKey2 = Base64.getEncoder().encodeToString(decodedKey.getBytes());
    }

}
