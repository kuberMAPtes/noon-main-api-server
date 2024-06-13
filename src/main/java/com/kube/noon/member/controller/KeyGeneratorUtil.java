package com.kube.noon.member.controller;

import java.security.SecureRandom;
import java.util.Base64;

public class KeyGeneratorUtil {

    public static String generateBase64UrlKey() {
        byte[] randomBytes = new byte[64];
        SecureRandom secureRandom = new SecureRandom();
        secureRandom.nextBytes(randomBytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(randomBytes);
    }

    public static void main(String[] args) {
        String secretKey = generateBase64UrlKey();
        System.out.println("Generated Base64URL Key: " + secretKey);
    }
}
