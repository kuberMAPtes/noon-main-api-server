package com.kube.noon.feed.service.recommend;

import com.kube.noon.feed.dto.MemberLikeTagDto;
import org.apache.mahout.cf.taste.impl.model.file.FileDataModel;
import org.apache.mahout.cf.taste.impl.neighborhood.NearestNUserNeighborhood;
import org.apache.mahout.cf.taste.impl.recommender.GenericUserBasedRecommender;
import org.apache.mahout.cf.taste.impl.similarity.PearsonCorrelationSimilarity;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.neighborhood.UserNeighborhood;
import org.apache.mahout.cf.taste.recommender.UserBasedRecommender;
import org.apache.mahout.cf.taste.similarity.UserSimilarity;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class FeedRecommendationMemberId {
    private static HashMap<Long, String> members;
    private static HashMap<Long, String> tagTexts;
    private static final String filePath = "src/main/java/com/kube/noon/feed/service/recommend/member_like_tags.csv";

    public static void initData(List<MemberLikeTagDto> memberLikeTagDtoList) {
        // Hash -> String으로 변환하기 위한 데이터값 삽입
        members = new HashMap<>();
        tagTexts = new HashMap<>();

        memberLikeTagDtoList = MinMaxScaler.tagCountScaler(memberLikeTagDtoList);

        try (FileWriter writer = new FileWriter(filePath)) {
            for (MemberLikeTagDto data : memberLikeTagDtoList) {
                String memberId = data.getMemberId();
                String tagText = data.getTagText();
                String row = memberId.hashCode() + "," + tagText.hashCode() + "," + data.getTagCount();

                members.put((long) memberId.hashCode(), memberId);
                tagTexts.put((long) tagText.hashCode(), tagText);

                writer.append(row);
                writer.append("\n");
            }

            System.out.println("CSV file is generated");

        } catch (IOException e) {
            System.out.println("Exception during generating CSV file  : " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static List<String> getMemberLikeTagsRecommendation(String memberId) {
        List<String> memberIdList = new ArrayList<>();

        try {
            DataModel model = new FileDataModel(new File(filePath));

            UserSimilarity similarity = new PearsonCorrelationSimilarity(model);
            UserNeighborhood neighborhood = new NearestNUserNeighborhood(2, similarity, model);
            UserBasedRecommender recommender = new GenericUserBasedRecommender(model, neighborhood, similarity);

            long[] mostSimlarUserIds = recommender.mostSimilarUserIDs(memberId.hashCode(), 3);

            for (long userId : mostSimlarUserIds) {
                memberIdList.add(members.get(userId));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return memberIdList;
    }
}
