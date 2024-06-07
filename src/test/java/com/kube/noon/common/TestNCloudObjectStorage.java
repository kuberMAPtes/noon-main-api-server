package com.kube.noon.common;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * NCloud의 Object Storage활용 테스트
 *
 * @author 허예지
 */
@Slf4j
@SpringBootTest
public class TestNCloudObjectStorage {

    @Autowired
    ObjectStorageAWS3S objectStorageAWS3S;

    @DisplayName("버킷의 파일 목록 가져오기")
    @Test
    void ListObject(){
        objectStorageAWS3S.getFileList();
    }



    @DisplayName("파일 업로드하기")
    @Test
    void addObject(){
        objectStorageAWS3S.uploadFile("./src/main/resources/images/BabyTiger.jpg");
    }


    @DisplayName("파일 삭제하기")
    @Test
    void deleteObject(){
        objectStorageAWS3S.deleteFile("BabyTiger.jpg");
    }



}
