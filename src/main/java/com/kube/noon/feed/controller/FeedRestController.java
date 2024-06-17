package com.kube.noon.feed.controller;

import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.kube.noon.common.ObjectStorageAPI;
import com.kube.noon.feed.dto.*;
import com.kube.noon.feed.service.FeedService;
import com.kube.noon.feed.service.FeedStatisticsService;
import com.kube.noon.feed.service.FeedSubService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

@Log4j2
@RestController
@RequiredArgsConstructor
@RequestMapping("/feed")
@Tag(name = "Feed", description = "피드 기능 API")
public class FeedRestController {
    private final FeedService feedService;
    private final FeedStatisticsService feedStatisticsService;
    private final FeedSubService feedSubService;

    private static final int PAGE_SIZE = 10;        // 일단 static final로 설정, 나중에 메타데이터로 뺄 예정

    @Operation(summary = "회원 피드 목록 조회", description = "회원이 작성한 피드 목록을 가져옵니다.")
    @GetMapping("/getFeedListByMember")
    public List<FeedSummaryDto> getMemberFeedList(
            @Parameter(description = "회원 ID") @RequestParam String memberId,
            @Parameter(description = "가져올 페이지(default = 1)") @RequestParam(required = false, defaultValue = "1") int page) {
        List<FeedSummaryDto> feedListByMember = feedService.getFeedListByMember(memberId, page - 1, PAGE_SIZE);

        return feedListByMember;
    }

    @Operation(summary = "건물 피드 목록 조회", description = "건물 내 작성된 피드 목록을 가져옵니다.")
    @GetMapping("/getFeedListByBuilding")
    public List<FeedSummaryDto> getBuildingFeedList(
            @Parameter(description = "추천 알고리즘을 위한 회원 ID") @RequestParam String memberId,
            @Parameter(description = "건물 ID") @RequestParam int buildingId,
            @Parameter(description = "가져올 페이지(default = 1)")@RequestParam(required = false, defaultValue = "1") int page) {
        List<FeedSummaryDto> feedListByBuilding = feedService.getFeedListByBuilding(memberId, buildingId, page - 1, PAGE_SIZE);

        return feedListByBuilding;
    }

    @Operation(summary = "회원의 좋아요 피드 목록 조회", description = "회원이 좋아요를 누른 피드 목록을 가져옵니다.")
    @GetMapping("/getFeedListByMemberLike")
    public List<FeedSummaryDto> getMemberLikeFeedList(
            @Parameter(description = "회원 ID") @RequestParam String memberId,
            @Parameter(description = "가져올 페이지(default = 1)") @RequestParam(required = false, defaultValue = "1") int page) {
        List<FeedSummaryDto> feedListByMemberLike = feedService.getFeedListByMemberLike(memberId, page - 1, PAGE_SIZE);

        return feedListByMemberLike;
    }

    @Operation(summary = "회원의 북마크 피드 목록 조회", description = "회원이 북마크를 등록한 피드 목록을 가져옵니다.")
    @GetMapping("/getFeedListByMemberBookmark")
    public List<FeedSummaryDto> getBookmarkFeedList(
            @Parameter(description = "회원 ID") @RequestParam String memberId,
            @Parameter(description = "가져올 페이지(default = 1)") @RequestParam(required = false, defaultValue = "1") int page) {
        List<FeedSummaryDto> feedListByMemberBookmark = feedService.getFeedListByMemberBookmark(memberId, page - 1, PAGE_SIZE);

        return feedListByMemberBookmark;
    }

    @Operation(summary = "회원의 구독한 건물 피드 목록 조회", description = "회원이 구독한 건물의 피드 목록을 조회합니다.")
    @GetMapping("/getFeedListByMemberSubscription")
    public List<FeedSummaryDto> getBuildingSubscriptionFeedList(
            @Parameter(description = "회원 ID") @RequestParam String memberId,
            @Parameter(description = "가져올 페이지(default = 1)") @RequestParam(required = false, defaultValue = "1") int page) {
        List<FeedSummaryDto> feedListByBuildingSubscription = feedService.getFeedListByBuildingSubscription(memberId, page - 1, PAGE_SIZE);

        return feedListByBuildingSubscription;
    }

    @Operation(summary = "피드 추가", description = "피드를 하나 추가합니다.")
    @PostMapping("/addFeed")
    public int addFeed(
            @RequestBody FeedDto feedDto) {
        int feedId = feedService.addFeed(feedDto);

        return feedId;
    }

    @Operation(summary = "피드 수정", description = "피드를 하나 수정합니다. 제목과 텍스트, 카테고리만 수정 가능합니다.")
    @PostMapping("/updateFeed")
    public int updateFeed(@RequestBody UpdateFeedDto updateFeedDto) {
        int feedId = feedService.updateFeed(updateFeedDto);

        return feedId;
    }

    @Operation(summary = "피드 삭제", description = "피드를 하나 삭제합니다.")
    @PostMapping("/deleteFeed/{feedId}")
    public int deleteFeed(@Parameter(description = "삭제할 피드 ID") @PathVariable int feedId) {
        int deleteFeedId = feedService.deleteFeed(feedId);

        return deleteFeedId;
    }

    @Operation(summary = "피드 상세보기", description = "피드를 하나 상세보기합니다.")
    @GetMapping("/detail")
    public FeedDto getFeed(@Parameter(description = "상세보기할 피드 ID") @RequestParam int feedId) {
        FeedDto getFeedDto = feedService.getFeedById(feedId);

        return getFeedDto;
    }
    
    @Operation(summary = "메인 피드 설정", description = "자신의 피드 중 메인 피드를 하나 설정합니다. (사용하는 정보 : 피드 ID, 회원 ID)")
    @PostMapping("/setMainFeed")
    public int setMainFeed(@RequestBody FeedDto feedDto) {
        int mainFeedId = feedService.setMainFeed(feedDto);

        return mainFeedId;
    }

    @Operation(summary = "피드 검색하기", description = "피드 제목이나 내용을 검색한 결과를 가져옵니다.")
    @GetMapping("/search")
    public List<FeedSummaryDto> searchFeed(@Parameter(description = "검색할 키워드") @RequestParam String keyword) {
        List<FeedSummaryDto> result = feedService.searchFeedList(keyword);

        return result;
    }

    @Operation(summary = "피드 조회수 증가", description = "피드의 조회수를 1 증가시킵니다.")
    @PostMapping("/viewCutUp/{feedId}")
    public int viewCutUp(@Parameter(description = "조회수를 증가시킬 피드 ID") @PathVariable("feedId") int feedId) {
        int cntUpFeedId = feedService.setViewCntUp(feedId);

        return cntUpFeedId;
    }

    @Operation(summary = "피드의 첨부파일 목록 조회", description = "피드에 첨부된 파일 목록을 가져옵니다.")
    @GetMapping("/getFeedAttachmentList")
    public List<FeedAttachmentDto> getFeedAttachmentList(@Parameter(description = "첨부파일을 조회할 피드 ID") @RequestParam int feedId) {
        List<FeedAttachmentDto> feedAttachmentDtoList = feedSubService.getFeedAttachmentList(feedId);

        return feedAttachmentDtoList;
    }

    @Operation(summary = "피드의 첨부파일 하나 조회", description = "피드에 첨부된 파일 하나를 만듭니다.")
    @GetMapping("/getFeedAttachment")
    public ResponseEntity<byte[]> getFeedAttachment(@Parameter(description = "첨부파일 ID") @RequestParam int attachmentId) {
        ResponseEntity<byte[]> resultEntity = feedSubService.getFeedAttachment(attachmentId);

        return resultEntity;
    }

    @Operation(summary = "피드 내 첨부파일 추가", description = "피드에 첨부파일 하나를 추가합니다.")
    @PostMapping("/addFeedAttachment/{feedId}")
    public int addFeedAttachment(
            @RequestParam("multipartFile") List<MultipartFile> multiFileList,
            @Parameter(description = "첨부파일을 추가할 피드ID") @PathVariable("feedId") int feedId) {
        int attachmentId = feedSubService.addFeedAttachment(feedId, multiFileList);

        return attachmentId;
    }

    @Operation(summary = "피드 내 첨부파일 삭제", description = "피드에 첨부파일 하나를 삭제합니다.")
    @PostMapping("/deleteFeedAttachment/{attachmentId}")
    public int deleteFeedAttachment(@Parameter(description = "삭제할 첨부파일 ID") @PathVariable("attachmentId") int attachmentId) {
        int deleteAttachmentId = feedSubService.deleteFeedAttachment(attachmentId);

        return deleteAttachmentId;
    }

    @Operation(summary = "피드의 좋아요 등록", description = "하나의 피드에 좋아요를 등록합니다.")
    @PostMapping("/addFeedLike/{feedId}/{memberId}")
    public int addFeedLike(
            @Parameter(description = "좋아요를 할 피드 ID") @PathVariable("feedId") int feedId,
            @Parameter(description = "좋아요를 누르는 회원 ID") @PathVariable("memberId") String memberId) {
        int zzimId = feedSubService.addFeedLike(feedId, memberId);

        return zzimId;
    }

    @Operation(summary = "피드의 좋아요 삭제", description = "등록한 피드의 좋아요를 삭제합니다.")
    @PostMapping("/deleteFeedLike/{feedId}/{memberId}")
    public int deleteFeedLike(
            @Parameter(description = "좋아요를 취소할 피드 ID") @PathVariable("feedId") int feedId,
            @Parameter(description = "좋아요를 취소하는 회원 ID") @PathVariable("memberId") String memberId) {
        int zzimId = feedSubService.deleteFeedLike(feedId, memberId);

        return zzimId;
    }

    @Operation(summary = "피드의 북마크 등록", description = "하나의 피드에 북마크를 등록합니다.")
    @PostMapping("/addbookmark/{feedId}/{memberId}")
    public int addFeedBookmark(
            @Parameter(description = "북마크를 등록할 피드 ID") @PathVariable("feedId") int feedId,
            @Parameter(description = "북마크를 등록하는 회원 ID") @PathVariable("memberId") String memberId) {
        int zzimId = feedSubService.addFeedBookmark(feedId, memberId);

        return zzimId;
    }

    @Operation(summary = "피드의 북마크 취소", description = "등록한 피드의 북마크를 취소합니다.")
    @PostMapping("/deleteBookmark/{feedId}/{memberId}")
    public int deleteFeedBookmark(
            @Parameter(description = "북마크를 취소할 피드 ID") @PathVariable("feedId") int feedId,
            @Parameter(description = "북마크를 취소하는 회원 ID") @PathVariable("memberId") String memberId) {
        int zzimId = feedSubService.deleteFeedBookmark(feedId, memberId);

        return zzimId;
    }

    @Operation(summary = "피드의 좋아요를 누른 회원 조회", description = "하나의 피드 내에 좋아요를 누르는 회원 목록을 조회합니다.")
    @GetMapping("/getFeedLikeList")
    public List<FeedLIkeMemberDto> getFeedLikeList(@Parameter(description = "목록을 조회할 피드 ID") @RequestParam int feedId) {
        return feedSubService.getFeedLikeList(feedId);
    }

    @Operation(summary = "피드 댓글 추가", description = "하나의 피드에 댓글을 추가합니다.")
    @PostMapping("/addFeedComment")
    public int addFeedComment(@RequestBody FeedCommentDto feedCommentDto) {
        return feedSubService.addFeedComment(feedCommentDto);
    }

    @Operation(summary = "피드 댓글 삭제", description = "댓글을 삭제합니다.")
    @PostMapping("/deleteFeedComment/{commentId}")
    public int deleteFeedComment(@Parameter(description = "삭제할 댓글 ID") @PathVariable("commentId") int commentId) {
        return feedSubService.deleteFeedComment(commentId);
    }

    @Operation(summary = "피드 댓글 수정", description = "댓글을 수정합니다.")
    @PostMapping("/updateFeedComment")
    public int updateFeedComment(@RequestBody FeedCommentDto feedCommentDto) {
        return feedSubService.updateFeedCommnet(feedCommentDto);
    }

    @Operation(summary = "피드 내 태그 목록 조회", description = "피드에 등록된 태그 목록을 가져옵니다.")
    @GetMapping("/getFeedTagList")
    public List<TagDto> getFeedTagList(@Parameter(description = "피드 목록을 가져올 피드 ID") @RequestParam int feedId) {
        return feedSubService.getFeedTagList(feedId);
    }

    @Operation(summary = "태그 추가", description = "피드 내 태그를 추가합니다.")
    @PostMapping("/addFeedTag")
    public int addFeedTag(@RequestBody AddTagDto addTagDto) {
        return feedSubService.addFeedTag(addTagDto.getFeedId(), addTagDto.getTagText());
    }

    @Operation(summary = "태그 삭제", description = "피드 내 태그를 삭제합니다.")
    @PostMapping("/deleteFeedTag")
    public int deleteFeedTag(@RequestBody AddTagDto addTagDto) {
        return feedSubService.deleteFeedTag(addTagDto.getFeedId(), addTagDto.getTagText());
    }

    @Operation(summary = "[통계] 건물별 조회수가 높은 피드 목록", description = "건물 내에서 조회수가 높은 피드 5개를 가져옵니다.")
    @GetMapping("/feedViewCuntByBuilding")
    public List<FeedViewCntByBuildingDto> getFeedViewCntByBuilding(@Parameter(description = "목록을 가져올 건물 ID") @RequestParam int buildingId) {
        return feedStatisticsService.getFeedViewCntByBuilding(buildingId);
    }

    @Operation(summary = "[통계] 인기 많은 태그 목록", description = "많이 사용되는 태그 5개를 가져옵니다.")
    @GetMapping("/feedCntByTag")
    public List<FeedCntByTagDto> getFeedViewCntByBuilding() {
        return feedStatisticsService.getFeedCntByTag();
    }

    @Operation(summary = "[통계] 건물별 인기도가 높은 피드 목록", description = "건물 내에서 인기도가 높은 피드 5개를 가져옵니다.")
    @GetMapping("/FeedPopularity")
    public List<FeedPopularityDto> getFeedPopularity(@RequestParam int buildingId) {
        return feedStatisticsService.getFeedPopularity(buildingId);
    }
}
