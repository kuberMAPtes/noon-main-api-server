package com.kube.noon.member.dto.util;

import java.util.Random;

public class RandomData {

    public static String getRandomPhoneNumber() {
        Random random = new Random();
        int firstPart = 600 + random.nextInt(400); // 600-999
        int secondPart = random.nextInt(10000); // 0000-9999
        int thirdPart = random.nextInt(10000); // 0000-9999
        return String.format("%03d-%04d-%04d", firstPart, secondPart, thirdPart);
    }

    public static String getRandomNickname() {
        Random random = new Random();
        int firstPart = 1000 + random.nextInt(9000); // 1000~9999
        int secondPart = random.nextInt(10000); // 0000~9999
        return String.format("noon_%04d_%04d", firstPart, secondPart);
    }
    //랜덤4자리번호만들기
    public static String getRandomAuthNumber() {
        Random rand = new Random();
        String randomNum = "";
        for (int i = 0; i < 4; i++) {
            String random = Integer.toString(rand.nextInt(10));
            randomNum += random;
        }

        return randomNum;
    }//end of createRandomNumber
}
