package com.kube.noon.feed.controller;

import com.kube.noon.feed.dto.*;
import com.kube.noon.feed.service.FeedService;
import com.kube.noon.feed.service.FeedStatisticsService;
import com.kube.noon.feed.service.FeedSubService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Log4j2
@RestController
@RequiredArgsConstructor
@RequestMapping("/feed")
public class FeedRestController {
    private final FeedService feedService;
    private final FeedStatisticsService feedStatisticsService;
    private final FeedSubService feedSubService;

    @GetMapping("/getFeedListByMember")
    public List<FeedSummaryDto> getMemberFeedList(@RequestParam String memberId) {
        List<FeedSummaryDto> feedListByMember = feedService.getFeedListByMember(memberId);

        return feedListByMember;
    }

    @GetMapping("/getFeedListByBuilding")
    public List<FeedSummaryDto> getBuildingFeedList(
            @RequestParam String memberId,
            @RequestParam int buildingId) {
        List<FeedSummaryDto> feedListByBuilding = feedService.getFeedListByBuilding(memberId, buildingId);

        return feedListByBuilding;
    }

    @GetMapping("/getFeedListByMemberLike")
    public List<FeedSummaryDto> getMemberLikeFeedList(@RequestParam String memberId) {
        List<FeedSummaryDto> feedListByMemberLike = feedService.getFeedListByMemberLike(memberId);

        return feedListByMemberLike;
    }

    @GetMapping("/getFeedListByMemberBookmark")
    public List<FeedSummaryDto> getBookmarkFeedList(@RequestParam String memberId) {
        List<FeedSummaryDto> feedListByMemberBookmark = feedService.getFeedListByMemberBookmark(memberId);

        return feedListByMemberBookmark;
    }

    @GetMapping("/getFeedListByMemberSubscription")
    public List<FeedSummaryDto> getBuildingSubscriptionFeedList(@RequestParam String memberId) {
        List<FeedSummaryDto> feedListByBuildingSubscription = feedService.getFeedListByBuildingSubscription(memberId);

        return feedListByBuildingSubscription;
    }

    @PostMapping("/addFeed")
    public int addFeed(@RequestBody FeedDto feedDto) {
        int feedId = feedService.addFeed(feedDto);

        return feedId;
    }

    @PostMapping("/updateFeed")
    public int updateFeed(@RequestBody FeedDto feedDto) {
        int feedId = feedService.updateFeed(feedDto);

        return feedId;
    }

    @PostMapping("/deleteFeed/{feedId}")
    public int deleteFeed(@PathVariable int feedId) {
        int deleteFeedId = feedService.deleteFeed(feedId);

        return deleteFeedId;
    }

    @GetMapping("/detail/{feedId}")
    public FeedDto getFeed(@PathVariable int feedId) {
        FeedDto getFeedDto = feedService.getFeedById(feedId);

        return getFeedDto;
    }

    // 필요 조건 : feedId, memberId
    @PostMapping("/setMainFeed")
    public int setMainFeed(@RequestBody FeedDto feedDto) {
        int mainFeedId = feedService.setMainFeed(feedDto);

        return mainFeedId;
    }

    @GetMapping("/search")
    public List<FeedSummaryDto> searchFeed(@RequestParam String keyword) {
        List<FeedSummaryDto> result = feedService.searchFeedList(keyword);

        return result;
    }

    @PostMapping("/viewCutUp/{feedId}")
    public int viewCutUp(@PathVariable("feedId") int feedId) {
        int cntUpFeedId = feedService.setViewCntUp(feedId);

        return cntUpFeedId;
    }

    @GetMapping("/getFeedAttachmentList/{feedId}")
    public List<FeedAttachmentDto> getFeedAttachmentList(@PathVariable("feedId") int feedId) {
        List<FeedAttachmentDto> result = feedSubService.getFeedAttachmentList(feedId);

        return result;
    }

    @PostMapping("/addFeedAttachment")
    public int addFeedAttachment(@RequestBody FeedAttachmentDto feedAttachmentDto) {
        int attachmentId = feedSubService.addFeedAttachment(feedAttachmentDto);

        return attachmentId;
    }

    @PostMapping("/deleteFeedAttachment/{attachmentId}")
    public int deleteFeedAttachment(@PathVariable("attachmentId") int attachmentId) {
        int deleteAttachmentId = feedSubService.deleteFeedAttachment(attachmentId);

        return deleteAttachmentId;
    }

    @PostMapping("/addFeedLike/{feedId}/{memberId}")
    public int addFeedLike(
            @PathVariable("feedId") int feedId,
            @PathVariable("memberId") String memberId) {
        int zzimId = feedSubService.addFeedLike(feedId, memberId);

        return zzimId;
    }

    @PostMapping("/deleteFeedLike/{feedId}/{memberId}")
    public int deleteFeedLike(
            @PathVariable("feedId") int feedId,
            @PathVariable("memberId") String memberId) {
        int zzimId = feedSubService.deleteFeedLike(feedId, memberId);

        return zzimId;
    }

    @PostMapping("/addbookmark/{feedId}/{memberId}")
    public int addFeedBookmark(
            @PathVariable("feedId") int feedId,
            @PathVariable("memberId") String memberId) {
        int zzimId = feedSubService.addFeedBookmark(feedId, memberId);

        return zzimId;
    }

    @PostMapping("/deleteBookmark/{feedId}/{memberId}")
    public int deleteFeedBookmark(
            @PathVariable("feedId") int feedId,
            @PathVariable("memberId") String memberId) {
        int zzimId = feedSubService.deleteFeedBookmark(feedId, memberId);

        return zzimId;
    }

    @GetMapping("/getFeedLikeList/{feedId}")
    public List<FeedLIkeMemberDto> getFeedLikeList(@PathVariable("feedId") int feedId) {
        return feedSubService.getFeedLikeList(feedId);
    }

    @PostMapping("/addFeedComment")
    public int addFeedComment(@RequestBody FeedCommentDto feedCommentDto) {
        return feedSubService.addFeedComment(feedCommentDto);
    }

    @PostMapping("/deleteFeedComment/{commentId}")
    public int deleteFeedComment(@PathVariable("commentId") int commentId) {
        return feedSubService.deleteFeedComment(commentId);
    }

    @PostMapping("/updateFeedComment")
    public int updateFeedComment(@RequestBody FeedCommentDto feedCommentDto) {
        return feedSubService.updateFeedCommnet(feedCommentDto);
    }

    @GetMapping("/feedTagList/{feedId}")
    public List<TagDto> getFeedTagList(@PathVariable("feedId") int feedId) {
        return feedSubService.getFeedTagList(feedId);
    }

    @PostMapping("/addFeedTag")
    public int addFeedTag(@RequestBody AddTagDto addTagDto) {
        return feedSubService.addFeedTag(addTagDto.getFeedId(), addTagDto.getTagText());
    }

    @PostMapping("/deleteFeedTag")
    public int deleteFeedTag(@RequestBody AddTagDto addTagDto) {
        return feedSubService.addFeedTag(addTagDto.getFeedId(), addTagDto.getTagText());
    }

    @GetMapping("/feedViewCuntByBuilding/{buildingId}")
    public List<FeedViewCntByBuildingDto> getFeedViewCntByBuilding(@PathVariable("buildingId") int buildingId) {
        return feedStatisticsService.getFeedViewCntByBuilding(buildingId);
    }

    @GetMapping("/feedCntByTag")
    public List<FeedCntByTagDto> getFeedViewCntByBuilding() {
        return feedStatisticsService.getFeedCntByTag();
    }

    @GetMapping("/FeedPopularity/{buildingId}")
    public List<FeedPopularityDto> getFeedPopularity(@PathVariable("buildingId") int buildingId) {
        return feedStatisticsService.getFeedPopularity(buildingId);
    }
}
