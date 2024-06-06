package com.kube.noon.feed.service;

import com.kube.noon.common.FileType;
import com.kube.noon.feed.dto.FeedAttachmentDto;
import com.kube.noon.feed.dto.FeedCommentDto;
import com.kube.noon.feed.dto.FeedLIkeMemberDto;
import com.kube.noon.member.dto.MemberProfileDto;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface FeedSubService {
    // FeedAttactmentDto : 피드 첨부파일 관련
    List<FeedAttachmentDto> getFeedAttachementListByFileType(FileType fileType);

    FeedAttachmentDto getFeedAttachment(int attachmentId);

    List<FeedAttachmentDto> getFeedAttachmentList(int feedId);

    int addFeedAttachment(FeedAttachmentDto feedAttachmentDto);

    int deleteFeedAttachment(int attachmentId);

    // Zzim : 피드의 좋아요, 북마크 관련
    int addFeedLike(int feedId, String memberId);

    int deleteFeedLike(int feedId, String memberId);

    int addFeedBookmark(int feedId, String memberId);

    int deleteFeedBookmark(int feedId, String memberId);

    List<FeedLIkeMemberDto> getFeedLikeList(int feedId);

    // FeedComment : 피드의 댓글 관련
    List<FeedCommentDto> getFeedCommentList(int feedId);

    int addFeedComment(FeedCommentDto feedCommentDto);

    int deleteFeedComment(int commentId);

    int updateFeedCommnet(FeedCommentDto feedCommentDto);
}
