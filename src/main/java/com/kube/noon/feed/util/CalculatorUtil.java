package com.kube.noon.feed.util;

import com.kube.noon.feed.dto.FeedDto;

public class CalculatorUtil {
    public static int calPopularity(FeedDto feed) {
        int viewCnt = feed.getViewCnt().intValue(); // 조회수
        int likeCount = feed.getLikeCount();
        int bookmarkCount = feed.getBookmarkCount();

        int result = viewCnt + 3 * likeCount + bookmarkCount;
        return result;
    }
}
