package com.kube.noon.customersupport.repository;

import com.kube.noon.common.ObjectStorageAWS3S;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;

@Slf4j
@Repository
public class AttachmentFilteringRepositoryImpl implements AttachmentFilteringRepository{


    ///Field
    @Autowired
    ObjectStorageAWS3S objectStorageAWS3S;



    ///Method
    @Override
    public String addBluredFile(String fileUrl) {

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
