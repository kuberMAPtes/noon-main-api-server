package com.kube.noon.feed.service.recommend;

import com.kube.noon.feed.dto.MemberLikeTagDto;
import com.kube.noon.feed.service.impl.FeedStatisticsServiceImpl;
import com.kube.noon.feed.util.MinMaxScaler;
import org.apache.mahout.cf.taste.impl.model.file.FileDataModel;
import org.apache.mahout.cf.taste.impl.neighborhood.NearestNUserNeighborhood;
import org.apache.mahout.cf.taste.impl.neighborhood.ThresholdUserNeighborhood;
import org.apache.mahout.cf.taste.impl.recommender.GenericItemBasedRecommender;
import org.apache.mahout.cf.taste.impl.recommender.GenericUserBasedRecommender;
import org.apache.mahout.cf.taste.impl.similarity.LogLikelihoodSimilarity;
import org.apache.mahout.cf.taste.impl.similarity.PearsonCorrelationSimilarity;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.neighborhood.UserNeighborhood;
import org.apache.mahout.cf.taste.recommender.RecommendedItem;
import org.apache.mahout.cf.taste.recommender.UserBasedRecommender;
import org.apache.mahout.cf.taste.similarity.UserSimilarity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;

@Service
public class FeedRecommendationService {

    @Autowired
    private FeedStatisticsServiceImpl feedStatisticsService;

    public void getMemberLikeTagsRecommendation() {
        HashMap<Integer, String> members = new HashMap<>();
        HashMap<Integer, String> tagTexts = new HashMap<>();

        List<MemberLikeTagDto> memberLikeTagDtoList = feedStatisticsService.getMemberLikeTag();
        memberLikeTagDtoList = MinMaxScaler.tagCountScaler(memberLikeTagDtoList);

        for (MemberLikeTagDto memberListTagDto : memberLikeTagDtoList) {
            System.out.println(memberListTagDto.getTagCount());
        }

        String filePath = "src/main/java/com/kube/noon/feed/service/recommend/member_like_tags.csv";

        try (FileWriter writer = new FileWriter(filePath)) {
            for (MemberLikeTagDto data : memberLikeTagDtoList) {
                String memberId = data.getMemberId();
                String tagText = data.getTagText();
                System.out.println("memberId : " + memberId + " tagText : " + tagText);

                writer.append(String.join(","
                        ,String.valueOf(memberId.hashCode())
                        ,String.valueOf(tagText.hashCode())
                        ,String.valueOf(data.getTagCount())));
                writer.append("\n");

                members.put(memberId.hashCode(), memberId);
                tagTexts.put(tagText.hashCode(), tagText);
            }

            System.out.println("CSV 파일 생성 완료");
        } catch (IOException e) {
            System.out.println("CSV 파일 생성 중 에러 발생 : " + e.getMessage());
            e.printStackTrace();
        }

        try {
            DataModel model = new FileDataModel(new File(filePath));

//            PearsonCorrelationSimilarity similarity = new PearsonCorrelationSimilarity(model);

//            GenericItemBasedRecommender recommender = new GenericItemBasedRecommender(model, similarity);
//            List<RecommendedItem> recommendations = recommender.recommend("member_1".hashCode(), 2);
//            for (RecommendedItem recommendation : recommendations) {
//                System.out.println("Item ID: " + recommendation.getItemID() + ", Score: " + recommendation.getValue());
//            }

//            NearestNUserNeighborhood neighborhood = new NearestNUserNeighborhood(100, similarity, model);
//            GenericUserBasedRecommender recommender = new GenericUserBasedRecommender(model, neighborhood, similarity);
//            List<RecommendedItem> recommendations = recommender.recommend("member_1".hashCode(), 3);

//            LogLikelihoodSimilarity sim = new LogLikelihoodSimilarity(model);
//            GenericItemBasedRecommender recommender = new GenericItemBasedRecommender(model, sim);
//            List<RecommendedItem> recommend = recommender.recommend("member_1".hashCode(), 3);

            UserSimilarity similarity = new PearsonCorrelationSimilarity(model);
            UserNeighborhood neighborhood = new ThresholdUserNeighborhood(0.1, similarity, model);
            UserBasedRecommender recommender = new GenericUserBasedRecommender(model, neighborhood, similarity);

            List<RecommendedItem> recommendations = recommender.recommend("member_1".hashCode(), 2);

            System.out.println(recommendations);
            for(RecommendedItem r : recommendations) {
                System.out.println("TagID : " + r.getItemID() + " Score : " + r.getValue());
            }


        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
