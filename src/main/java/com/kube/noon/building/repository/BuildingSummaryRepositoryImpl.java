package com.kube.noon.building.repository;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import org.springframework.web.reactive.function.client.WebClient;

@Slf4j
@Repository
public class BuildingSummaryRepositoryImpl implements BuildingSummaryRepository {


    ///Field
    private static final String SUMMARY_ACCESS_KEY_HEADER = "X-NCP-APIGW-API-KEY-ID";
    private static final String SUMMARY_SECRET_KEY_HEADER = "X-NCP-APIGW-API-KEY";
    private static final String CLOVA_SUMMARY_API_URL = "https://naveropenapi.apigw.ntruss.com/text-summary/v1/summarize";
    private static final String SUMMARY_RESPONSE_LINE_COUNT = "1";// 1줄로 요약

    @Value("${summary.naver.access-key}")
    private String accessKey;

    @Value("${summary.naver.secret-key}")
    private String secretKey ;


    ///Method
    @Override
    public String findFeedAISummary(String title, String feedText) {

        log.info("title={}", title);
        log.info("feedText={}", feedText);

        WebClient client = WebClient.create(CLOVA_SUMMARY_API_URL);

        String requestBody = """
                {
                  "document": {
                    "title": "%s",
                    "content": "%s"
                  },
                  "option": {
                    "language": "ko",
                    "model": "news",
                    "tone": 3,
                    "summaryCount": %s
                  }
                }
                """.formatted(title, feedText, SUMMARY_RESPONSE_LINE_COUNT);

        log.info("requestBody={}", requestBody);


        return client.post()
                .header(SUMMARY_ACCESS_KEY_HEADER, this.accessKey)
                .header(SUMMARY_SECRET_KEY_HEADER, secretKey)
                .header("Content-Type", "application/json")
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(String.class)
                .block(); //테스트 코드에서는 동기식으로 작성함. 추후 수정


    }
}
