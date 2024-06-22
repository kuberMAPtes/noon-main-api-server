package com.kube.noon.common;

import com.amazonaws.SdkClientException;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.*;

/**
 * AWS S3의 Java SDK를 이용해 NCloud Object Storage를 활용하는 필드 및 메서드 작성
 * 
 * @author 허예지
 */
@Slf4j
@Component
public class ObjectStorageAWS3S {


    
    ///Field
    private final String endPoint = "https://kr.object.ncloudstorage.com";
    private final String regionName = "kr-standard";

    private final String accessKey;
    private final String secretKey;
    private final String bucketName;

    private final AmazonS3 s3;



    ///Constructor
    public ObjectStorageAWS3S(
            @Value("${api.auth.access-key.yj}") String accessKey,
            @Value("${api.auth.secret-key.yj}") String secretKey,
            @Value("${bucket.name}") String bucketName) {

        this.accessKey = accessKey;
        this.secretKey = secretKey;
        this.bucketName = bucketName;

        this.s3 = AmazonS3ClientBuilder.standard()
                .withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration(endPoint, regionName))
                .withCredentials(new AWSStaticCredentialsProvider(new BasicAWSCredentials(this.accessKey, this.secretKey)))
                .build();

    }




    ///Method
    /**
     * 버킷의 폴더 및 파일 목록 조회
     *
     */
    public void getFileList(){

        // list all in the bucket
        try {
            ListObjectsRequest listObjectsRequest = new ListObjectsRequest()
                    .withBucketName(bucketName)
                    .withMaxKeys(300);

            ObjectListing objectListing = s3.listObjects(listObjectsRequest);

            System.out.println("Object List:");
            while (true) {
                for (S3ObjectSummary objectSummary : objectListing.getObjectSummaries()) {
                    System.out.println("    name=" + objectSummary.getKey() + ", size=" + objectSummary.getSize() + ", owner=" + objectSummary.getOwner().getId());
                }

                if (objectListing.isTruncated()) {
                    objectListing = s3.listNextBatchOfObjects(objectListing);
                } else {
                    break;
                }
            }
        } catch (AmazonS3Exception e) {
            System.err.println(e.getErrorMessage());
            System.exit(1);
        }

        // top level folders and files in the bucket
        try {
            ListObjectsRequest listObjectsRequest = new ListObjectsRequest()
                    .withBucketName(bucketName)
                    .withDelimiter("/")
                    .withMaxKeys(300);

            ObjectListing objectListing = s3.listObjects(listObjectsRequest);

            System.out.println("Folder List:");
            for (String commonPrefixes : objectListing.getCommonPrefixes()) {
                System.out.println("    name=" + commonPrefixes);
            }

            System.out.println("File List:");
            for (S3ObjectSummary objectSummary : objectListing.getObjectSummaries()) {
                System.out.println("name=" + objectSummary.getKey() + ", size=" + objectSummary.getSize() + ", owner=" + objectSummary.getOwner().getId());
            }
        } catch (AmazonS3Exception e) {
            e.printStackTrace();
        } catch(SdkClientException e) {
            e.printStackTrace();
        }

    }/// end of getFileList


    /**
     * 버킷에 sample-folder/ 폴더 생성 후 파일 업로드
     * 
     * @param filePath 업로드할 파일의 로컬 경로
     * @return Object Storage에 저장된 Url (Object Storage -> 버킷 -> 상세 정보 -> Link)
     */
    public String uploadFile(String filePath){
        log.info("filePath={}", filePath);

        //Object Storage에 블러 파일 업로드
        String objectName = filePath.substring(filePath.lastIndexOf('/') + 1);
        log.info("Object Name={}",objectName);

        try {

            PutObjectRequest putObjectRequestWithACL = new PutObjectRequest(bucketName + "/blured-images", objectName, new File(filePath))
                    .withCannedAcl(CannedAccessControlList.PublicRead);

            s3.putObject(putObjectRequestWithACL);
            System.out.format("Object %s has been created with public-read ACL.\n", objectName);

            
            String uploadedFileUrl = "https://kr.object.ncloudstorage.com/"+
                    bucketName+"/blured-images/"+
                    filePath.substring(filePath.lastIndexOf('/') + 1);
            log.info("uploadedFileUrl={}", uploadedFileUrl);

            return uploadedFileUrl;

        } catch (AmazonS3Exception e) {
            e.printStackTrace();
        } catch(SdkClientException e) {
            e.printStackTrace();
        }

        return null;

    }/// end of uploadFile


    /**
     * 공지사항 이미지 업로드
     * @param filePath
     * @return
     */
    public String uploadNoticeFile(String filePath){ // TODO: uploadFile로 통합(코드 중복)
        log.info("filePath={}", filePath);

        // 경로 입력 두가지 경우 고려: /, \
        int lastIndexForwardSlash = filePath.lastIndexOf('/');
        int lastIndexBackSlash = filePath.lastIndexOf('\\');
        int lastIndex = Math.max(lastIndexForwardSlash, lastIndexBackSlash);

        //Object Storage에 블러 파일 업로드
        String objectName = filePath.substring(lastIndex + 1);
        log.info("Object Name={}",objectName);

        try {

            PutObjectRequest putObjectRequestWithACL = new PutObjectRequest(bucketName + "/feed-attachment", objectName, new File(filePath))
                    .withCannedAcl(CannedAccessControlList.PublicRead);

            s3.putObject(putObjectRequestWithACL);
            System.out.format("Object %s has been created with public-read ACL.\n", objectName);


            String uploadedFileUrl = "https://kr.object.ncloudstorage.com/"+
                    bucketName+"/feed-attachment/"+objectName;
            log.info("uploadedFileUrl={}", uploadedFileUrl);

            return uploadedFileUrl;

        } catch (AmazonS3Exception e) {
            e.printStackTrace();
        } catch(SdkClientException e) {
            e.printStackTrace();
        }

        return null;

    }/// end of uploadNoticeFile






    /**
     * Object Storage에서 파일 삭제
     *
     */
    public void deleteFile(String objectName){

        try {
            s3.deleteObject(bucketName, objectName);
            System.out.format("Object %s has been deleted.\n", objectName);
        } catch (AmazonS3Exception e) {
            e.printStackTrace();
        } catch(SdkClientException e) {
            e.printStackTrace();
        }

    }/// end of deleteFile


    /**
     * Object Storage 접근 권한 주기
     */
    public void putObjectACL(){

    }
}
