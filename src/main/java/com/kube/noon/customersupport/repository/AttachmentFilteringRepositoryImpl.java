package com.kube.noon.customersupport.repository;

import com.kube.noon.common.ObjectStorageAWS3S;
import com.kube.noon.feed.domain.FeedAttachment;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import org.springframework.web.reactive.function.client.WebClient;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Repository
public class AttachmentFilteringRepositoryImpl implements AttachmentFilteringRepository{


    ///Field
    @Autowired
    ObjectStorageAWS3S objectStorageAWS3S;

    private static final String GREENEYE_SECRET_KEY_HEADER = "X-GREEN-EYE-SECRET";

    @Value("${greeneye.apigw.invoke.url}")
    private String apigwUrl;

    @Value("${greeneye.naver.secret-key}")
    private String secretKey ;

    private static final String HARMFUL_LEVEL_NORMAL = "normal";
    private static final String HARMFUL_LEVEL_SEXY = "sexy";
    private static final String HARMFUL_LEVEL_ADULT = "adult";
    private static final String HARMFUL_LEVEL_PORN = "porn";



    ///Method
    /**
     * AIaaS 활용 유해사진 필터링 (NCloud Green Eye)
     *
     * @param feedAttachmentList fileType이 Image인 모든 첨부파일
     * @return AIaaS에 의해 1차 필터링 된 피드첨부파일 리스트
     */
    @Override
    public List<FeedAttachment> findBadImageListByAI(List<FeedAttachment> feedAttachmentList) {
        log.info("feedAttachmentList={}", feedAttachmentList);



        WebClient client = WebClient.create(apigwUrl);
        List<FeedAttachment> badImageList = new ArrayList<>();

        // Green Eye 유해성 판별은 요청 1회당 1개의 이미지만 가능
        for(FeedAttachment feedAttachment : feedAttachmentList){

            if(!feedAttachment.isActivated()) continue;

            String requestBody = """
                {
                  "version": "V1",
                  "requestId": "%s",
                  "timestamp": 0,
                  "images": [
                    {
                      "name": "%s",
                      "url": "%s"
                    }
                  ]
                }
                """.formatted(feedAttachment.getAttachmentId(), feedAttachment.getAttachmentId(), feedAttachment.getFileUrl());

            log.info("requestBody={}", requestBody);


            // Green Eye Request
            JSONObject response = new JSONObject(
                    client.post()
                            .header(GREENEYE_SECRET_KEY_HEADER, this.secretKey)
                            .header("Content-Type", "application/json")
                            .bodyValue(requestBody)
                            .retrieve()
                            .bodyToMono(String.class)
                            .block()
                    );

            // 유해성 결과 체크, 유해 사진만 모으기
            if(checkHarmful(response)){
                badImageList.add(feedAttachment);
            }

        }

        return badImageList;

    }///end of findFilteredListByAI


    /**
     * 필터링된 유해사진을 페이지로 나누어 제공
     * Pageable을 사용한 페이징 로직 구현 (JPARepository미사용에 따른)
     *
     * @param feedAttachmentList 유해성을 검사할 피드첨부파일 리스트
     * @param pageable 페이징 정보를 담은 객체
     * @return 페이징한 결과 Page
     */
    @Override
    public Page<FeedAttachment> findBadImageListByAI(List<FeedAttachment> feedAttachmentList, Pageable pageable) {

        List<FeedAttachment> badImageList = findBadImageListByAI(feedAttachmentList);

        int pageSize = pageable.getPageSize();
        int currentPage = pageable.getPageNumber();
        int startItem = currentPage * pageSize;
        List<FeedAttachment> pagedList;


        // 해당 페이지에 목록 없을때는 빈 리스트
        if (badImageList.size() < startItem) {
            pagedList = List.of();
        } else {
            int endItem = Math.min(startItem + pageSize, badImageList.size());
            pagedList = badImageList.subList(startItem, endItem);
        }

        return new PageImpl<>(pagedList, PageRequest.of(currentPage, pageSize), badImageList.size());

    }/// end of findBadImageListByAI



    @Override
    public String addBluredFile(String fileUrl) {

        log.info("fileUrl={}",fileUrl);

        try {
            //블러 이미지 생성
            String blurredImageLocation = makeBlurredImage(fileUrl);

            //블러 이미지 Object Storage에 저장
            String blurredFileUrl = objectStorageAWS3S.uploadFile(blurredImageLocation);

            return blurredFileUrl;

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }///end of addBluredFile



    /**
     * Adult나 Porn에 가까운 사진이라는 응답이 왔는지 체크
     *
     * @param response GreenEye로부터 받은 응답 Object
     * @return Adult나 Porn라면 true, Normal이나 Sexy라면 false
     */
    public boolean checkHarmful(JSONObject response){

        String HarmfulLevel[] = {HARMFUL_LEVEL_NORMAL,HARMFUL_LEVEL_SEXY,HARMFUL_LEVEL_ADULT,HARMFUL_LEVEL_PORN};
        JSONArray images = response.getJSONArray("images");
        JSONObject result = images.getJSONObject(0).getJSONObject("result");
        double determinedConfidence = images.getJSONObject(0).getDouble("confidence");
        double confidence=0.0;

        for(String level : HarmfulLevel){

            confidence = result.getJSONObject(level).getDouble("confidence");

            if(determinedConfidence==confidence){
                if( level.equals(HARMFUL_LEVEL_ADULT) || level.equals(HARMFUL_LEVEL_PORN) ){
                    log.info("유해 사진. 유해수준={}", level);
                    log.info("유해성 점수={}", confidence);
                    return true;
                }
            }
        }

        return false;

    }/// end of checkHarmful



    /**
     * url의 이미지에 대한 블러 이미지를 생성한다.
     *
     * @param fileUrl NCloud Object Storage에서 Object 상세 보기에 명시된 Url
     * @return 생성한 블러 이미지가 저장된 로컬 경로
     *
     * @author 허예지
     */
    public String makeBlurredImage(String fileUrl) throws IOException {

        URL imageUrl = new URL(fileUrl);
        BufferedImage input = ImageIO.read(imageUrl);

        Color color[];

        BufferedImage output = new BufferedImage(input.getWidth(), input.getHeight(), BufferedImage.TYPE_INT_RGB);
        int i = 0;
        int max = 400, rad = 10;
        int a1 = 0, r1 = 0, g1 = 0, b1 = 0;
        color = new Color[max];

        //블러링 작업
        int x = 1, y = 1, x1, y1, ex = 5, d = 0;
        for (x = rad; x < input.getHeight() - rad; x++) {
            for (y = rad; y < input.getWidth() - rad; y++) {

                for (x1 = x - rad; x1 < x + rad; x1++) {
                    for (y1 = y - rad; y1 < y + rad; y1++) {
                        color[i++] = new Color(input.getRGB(y1, x1));
                    }
                }
                i = 0;

                for (d = 0; d < max; d++) {
                    a1 = a1 + color[d].getAlpha();
                }
                a1 = a1 / (max);

                for (d = 0; d < max; d++) {
                    r1 = r1 + color[d].getRed();
                }
                r1 = r1 / (max);

                for (d = 0; d < max; d++) {
                    g1 = g1 + color[d].getGreen();
                }
                g1 = g1 / (max);

                for (d = 0; d < max; d++) {
                    b1 = b1 + color[d].getBlue();
                }
                b1 = b1 / (max);
                int sum1 = (a1 << 24) + (r1 << 16) + (g1 << 8) + b1;
                output.setRGB(y, x, (int) (sum1));

            }
        }

        // 블러 사진을 일단 로컬에 저장
        String blurredImageLocation = "./src/main/resources/images/blured-image.jpg";
        ImageIO.write(output, "jpg", new File(blurredImageLocation));
        log.info("블러 처리 완료. 블러 파일 경로={}",blurredImageLocation);

        return blurredImageLocation;

    }//end of makeBlurredImage
}
