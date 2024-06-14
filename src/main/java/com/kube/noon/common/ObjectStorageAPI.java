package com.kube.noon.common;

import com.amazonaws.SdkClientException;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.*;
import org.apache.commons.codec.binary.Hex;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.FileEntity;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.net.URI;
import java.net.URLEncoder;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import java.util.TreeMap;

@Component
public class ObjectStorageAPI {

    private String BUCKET_NAME;

    private String ACCESS_KEY;

    private String SECRET_KEY;

    private static final String REGION_NAME = "kr-standard";

    private static final String ENDPOINT = "https://kr.object.ncloudstorage.com";

    private AmazonS3 s3;

    public ObjectStorageAPI(
            @Value("${objectstorage.bucket-name}") String bucketName,
            @Value("${objectstorage.access-key}") String accessKey,
            @Value("${objectstorage.secret-key}") String secretKey
    ) {
        this.BUCKET_NAME = bucketName;
        this.ACCESS_KEY = accessKey;
        this.SECRET_KEY = secretKey;

        s3 = AmazonS3ClientBuilder.standard()
                .withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration(ENDPOINT, REGION_NAME))
                .withCredentials(new AWSStaticCredentialsProvider(new BasicAWSCredentials(ACCESS_KEY, SECRET_KEY)))
                .build();
    }

    // 버킷 파일 URL 생성
    public String getBucketFileUrl(String key) {
        String fileUrl = s3.getUrl(BUCKET_NAME, key).toString();

        return fileUrl;
    }

    // MultipartFile putObject
    public String putObject(String objectName, MultipartFile multipartFile) throws Exception {
        String fileUrl = getBucketFileUrl(objectName);
        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentType(multipartFile.getContentType());
        objectMetadata.setContentLength(multipartFile.getSize());
        PutObjectRequest putObjectRequest = new PutObjectRequest(BUCKET_NAME, objectName,multipartFile.getInputStream(), objectMetadata);

        try {
            s3.putObject(putObjectRequest);
        } catch (AmazonS3Exception e) {
            e.printStackTrace();
        } catch(SdkClientException e) {
            e.printStackTrace();
        }

        return fileUrl;
    }

    public
}
