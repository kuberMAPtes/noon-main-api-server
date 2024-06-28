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

    @Value("${file.upload-dir}")
    private String uploadDir;

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
    public String addBlurredFile(String fileUrl, int attachmentId, int blurIntensity) {

        log.info("fileUrl={}",fileUrl);

        try {
            //블러 이미지 생성
            String blurredImageLocation = makeBlurredImage(fileUrl,attachmentId, blurIntensity);

            //블러 이미지 Object Storage에 저장
            String blurredFileUrl = objectStorageAWS3S.uploadFile(blurredImageLocation);

            return blurredFileUrl;

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }///end of addBlurredFile



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
    public String   makeBlurredImage(String fileUrl, int attachmentId, int blurIntensity) throws IOException {
        URL imageUrl = new URL(fileUrl);
        BufferedImage input = ImageIO.read(imageUrl);

        int radius = 5; // 블러 반경
        int iterations = 5; // 블러 반복 횟수

        switch (blurIntensity){
            //1: 약, 2: 중, 3: 강
            case 1 :
                radius = 2;
                iterations = 2;
                break;
            case 2:
                radius = 5;
                iterations = 5;
                break;
            case 3:
                radius = 8;
                iterations = 8;
                break;
            default:
                break;
        }

        BufferedImage output = applyBoxBlur(input, radius, iterations);

        // 블러 사진을 일단 로컬에 저장
        //String blurredImageLocation = "./src/main/resources/images/blurred-"+attachmentId+".jpg";
        String blurredImageLocation = uploadDir+"-"+attachmentId+".jpg";


        ImageIO.write(output, "jpg", new File(blurredImageLocation));
        log.info("블러 처리 완료. 블러 파일 경로={}",blurredImageLocation);

        return blurredImageLocation;
    }

    private BufferedImage applyBoxBlur(BufferedImage input, int radius, int iterations) {
        int width = input.getWidth();
        int height = input.getHeight();
        BufferedImage blurredImage = new BufferedImage(width, height, input.getType());

        for (int i = 0; i < iterations; i++) {
            boxBlur(input, blurredImage, radius);
            input = blurredImage;
        }

        return blurredImage;
    }

    private void boxBlur(BufferedImage input, BufferedImage output, int radius) {
        int width = input.getWidth();
        int height = input.getHeight();

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int r = 0, g = 0, b = 0, a = 0;
                int count = 0;

                for (int dy = -radius; dy <= radius; dy++) {
                    for (int dx = -radius; dx <= radius; dx++) {
                        int nx = x + dx;
                        int ny = y + dy;

                        if (nx >= 0 && nx < width && ny >= 0 && ny < height) {
                            Color color = new Color(input.getRGB(nx, ny), true);
                            r += color.getRed();
                            g += color.getGreen();
                            b += color.getBlue();
                            a += color.getAlpha();
                            count++;
                        }
                    }
                }

                int newR = r / count;
                int newG = g / count;
                int newB = b / count;
                int newA = a / count;

                Color newColor = new Color(newR, newG, newB, newA);
                output.setRGB(x, y, newColor.getRGB());
            }
        }
    }


}
