package com.kube.noon.feed.service;

import com.kube.noon.common.FileType;
import com.kube.noon.feed.dto.FeedAttachmentDto;
import com.kube.noon.feed.dto.FeedCommentDto;
import com.kube.noon.feed.dto.FeedLIkeMemberDto;
import com.kube.noon.feed.dto.TagDto;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * 피드 자체 기능 외 다른 기능을 service로 구현하였다.
 * 추가, 수정, 삭제애 대한 return 값은 대상 테이블의 PK 값이다.
 * 예시 ) 첨부파일을 추가함 : 그 첨부파일에 대한 attachment_id를 return한다.
 */
@Service
public interface FeedSubService {
    /*  FeedAttactmentDto - 피드 첨부파일 관련 */
    // 첨부파일 타입별로 첨부파일을 가져온다.
    List<FeedAttachmentDto> getFeedAttachementListByFileType(FileType fileType);

    // 첨부파일 하나를 가져온다. : 바이너리 데이터
    ResponseEntity<byte[]> getFeedAttachment(int attachmentId);

    // 첨부파일 데이터 하나를 가져온다.
    FeedAttachmentDto getFeedAttachmentDto(int attachmentId);

    // 피드와 연관있는 첨부파일 목록을 가져온다.
    List<FeedAttachmentDto> getFeedAttachmentList(int feedId);

    // 피드에 첨부파일 하나를 추가한다. (return : feedId)
    int addFeedAttachment(int feedId, List<MultipartFile> multipartFileList);

    // 피드에 첨부파일 하나를 삭제한다.
    int deleteFeedAttachment(int attachmentId);

    /* Zzim - 피드의 좋아요, 북마크 관련 */
    // 피드에 좋아요를 추가한다.
    int addFeedLike(int feedId, String memberId);

    // 피드에 좋아요를 취소한다.
    int deleteFeedLike(int feedId, String memberId);

    // 피드에 북마크를 추가한다.
    int addFeedBookmark(int feedId, String memberId);

    // 피드에 북마크를 취소한다.
    int deleteFeedBookmark(int feedId, String memberId);

    // 피드에 좋아요를 누른 회원의 목록을 가져온다.
    List<FeedLIkeMemberDto> getFeedLikeList(int feedId);

    /* FeedComment : 피드의 댓글 관련 */
    // 피드에 달린 댓글 리스트를 가져온다.
    List<FeedCommentDto> getFeedCommentList(int feedId);

    // 피드에 댓글을 하나 추가한다.
    FeedCommentDto addFeedComment(FeedCommentDto feedCommentDto);

    // 댓글을 삭제한다.
    int deleteFeedComment(int commentId);

    // 피드의 댓글을 수정한다.
    int updateFeedCommnet(FeedCommentDto feedCommentDto);

    /* Tag, TagFeed : 피드의 태그 관련 */
    // 피드에 있는 태그 리스트를 가져온다.
    List<TagDto> getFeedTagList(int feedId);

    // 태그를 하나 추가한다.
    int addFeedTag(int feedId, String tagText);

    // 태그를 삭제한다.
    int deleteFeedTag(int feedId, String tagText);
}
