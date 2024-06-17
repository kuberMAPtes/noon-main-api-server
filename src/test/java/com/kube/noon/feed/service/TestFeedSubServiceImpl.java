package com.kube.noon.feed.service;

import com.kube.noon.common.FileType;
import com.kube.noon.common.zzim.Zzim;
import com.kube.noon.common.zzim.ZzimRepository;
import com.kube.noon.common.zzim.ZzimType;
import com.kube.noon.feed.domain.FeedAttachment;
import com.kube.noon.feed.domain.FeedComment;
import com.kube.noon.feed.dto.FeedAttachmentDto;
import com.kube.noon.feed.dto.FeedCommentDto;
import com.kube.noon.feed.dto.FeedLIkeMemberDto;
import com.kube.noon.feed.dto.TagDto;
import com.kube.noon.feed.repository.FeedAttachmentRepository;
import com.kube.noon.feed.repository.FeedCommentRepository;
import com.kube.noon.feed.service.impl.FeedServiceImpl;
import com.kube.noon.feed.service.impl.FeedSubServiceImpl;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@Log4j2
@SpringBootTest
@ActiveProfiles("winterhana")
public class TestFeedSubServiceImpl {
    @Autowired
    private FeedServiceImpl feedServiceImpl;
    @Autowired
    private FeedSubServiceImpl feedSubServiceImpl;
    @Autowired
    private FeedAttachmentRepository feedAttachmentRepository;
    @Autowired
    private ZzimRepository zzimRepository;
    @Autowired
    private FeedCommentRepository feedCommentRepository;

    /* -------------- 1. 피드 첨부 파일 관련 테스트 -------------- */

    /**
     * 피드 첨부파일을 가져오는 것에 대한 테스트를 진행한다.
     * feed_id = 10001을 기준으로 진행한다.
     */
    @Transactional
    @Test
    public void getFeedAttachmentTest() {
        List<FeedAttachmentDto> feedAttachmentList = feedSubServiceImpl.getFeedAttachmentList(10001);

        assertThat(feedAttachmentList).isNotNull();
        assertThat(feedAttachmentList.isEmpty()).isFalse();
        assertThat(feedAttachmentList.size()).isGreaterThan(0);
    }

    /**
     * 피드에 첨부 파일을 추가한다.
     * feed_id = 10000을 기준으로 한다.
     */
    @Transactional
    @Test
    public void addFeedAttachmentTest() {
//        FeedAttachmentDto feedAttachmentDto = FeedAttachmentDto.builder()
//                .feedId(10000)
//                .fileUrl("https://example.com/file_test.jpg")
//                .fileType(FileType.PHOTO)
//                .blurredFileUrl(null)
//                .activated(true)
//                .build();
//
//        int attachmentId = feedSubServiceImpl.addFeedAttachment(feedAttachmentDto);
//
//        FeedAttachmentDto getFeedAttachmentDto = feedSubServiceImpl.getFeedAttachment(attachmentId);
//
//        assertThat(getFeedAttachmentDto).isNotNull();
//        assertThat(getFeedAttachmentDto.getFeedId()).isEqualTo(10000);
//        assertThat(getFeedAttachmentDto.getFileUrl()).isEqualTo("https://example.com/file_test.jpg");
    }

    /**
     * 정해진 첨부파일 번호를 통해 첨부파일을 삭제한다.
     * attachment_id = 10000을 기준으로 한다.
     */
    @Transactional
    @Test
    public void deleteAttachmentTest() {
        int attachmentId = feedSubServiceImpl.deleteFeedAttachment(10000);

        FeedAttachment getFeedAttachment = feedAttachmentRepository.findByAttachmentId(attachmentId);

        assertThat(getFeedAttachment.isActivated()).isFalse();
    }

    /**
     * 원하는 타입의 첨부파일을 가져온다
     * 여기서는 FileType.PHOTO를 기준으로 한다.
     */
    @Transactional
    @Test
    public void getFeedAttachementListByFileTypeTest() {
        List<FeedAttachmentDto> feedAttachmentDtoList = feedSubServiceImpl.getFeedAttachementListByFileType(FileType.PHOTO);

        assertThat(feedAttachmentDtoList).isNotNull();
        assertThat(feedAttachmentDtoList).isNotEmpty();
        assertThat(feedAttachmentDtoList.size()).isGreaterThan(0);

        for (FeedAttachmentDto feedAttachmentDto : feedAttachmentDtoList) {
            log.info(feedAttachmentDto);
        }
    }

    /* -------------- 2. 피드의 좋아요, 북마크 관련 테스트 -------------- */

    /**
     * 피드에 좋아요를 하나 추가한다.
     */
    @Transactional
    @Test
    public void addFeedLikeTest() {
        int feedId = 10000;
        String memberId = "member_10";

        int zzimId = feedSubServiceImpl.addFeedLike(feedId, memberId);

        Optional<Zzim> zzim = zzimRepository.findById(zzimId);

        log.info(zzim.orElseGet(null).getZzimId());
        assertThat(zzim.orElseGet(null).getFeedId()).isEqualTo(feedId);
        assertThat(zzim.orElseGet(null).getMemberId()).isEqualTo(memberId);
        assertThat(zzim.orElseGet(null).getZzimType()).isEqualTo(ZzimType.LIKE);
    }

    /**
     * 피드의 좋아요를 취소한다.
     */
    @Transactional
    @Test
    public void deleteFeedLikeTest() {
        int feedId = 10000;
        String memberId = "member_1";

        int zzimId = feedSubServiceImpl.deleteFeedLike(feedId, memberId);

        Optional<Zzim> zzim = zzimRepository.findById(zzimId);

        log.info(zzim.orElseGet(null).getZzimId());
        assertThat(zzim.orElseGet(null).getFeedId()).isEqualTo(feedId);
        assertThat(zzim.orElseGet(null).getMemberId()).isEqualTo(memberId);
        assertThat(zzim.orElseGet(null).getZzimType()).isEqualTo(ZzimType.LIKE);
        assertThat(zzim.orElseGet(null).isActivated()).isFalse();
    }

    /**
     * 피드의 북마크를 추가한다.
     */
    @Transactional
    @Test
    public void addFeedBookmarkTest() {
        int feedId = 10000;
        String memberId = "member_10";

        int zzimId = feedSubServiceImpl.addFeedBookmark(feedId, memberId);

        Optional<Zzim> zzim = zzimRepository.findById(zzimId);

        log.info(zzim.orElseGet(null).getZzimId());
        assertThat(zzim.orElseGet(null).getFeedId()).isEqualTo(feedId);
        assertThat(zzim.orElseGet(null).getMemberId()).isEqualTo(memberId);
        assertThat(zzim.orElseGet(null).getZzimType()).isEqualTo(ZzimType.BOOKMARK);
    }

    /**
     * 피드의 북마크를 취소한다.
     */
    @Transactional
    @Test
    public void deleteFeedBookmakrTest() {
        int feedId = 10002;
        String memberId = "member_1";

        int zzimId = feedSubServiceImpl.deleteFeedBookmark(feedId, memberId);

        Optional<Zzim> zzim = zzimRepository.findById(zzimId);

        log.info(zzim.orElseGet(null).getZzimId());
        assertThat(zzim.orElseGet(null).getFeedId()).isEqualTo(feedId);
        assertThat(zzim.orElseGet(null).getMemberId()).isEqualTo(memberId);
        assertThat(zzim.orElseGet(null).getZzimType()).isEqualTo(ZzimType.BOOKMARK);
        assertThat(zzim.orElseGet(null).isActivated()).isFalse();
    }

    /**
     *  피드에 좋아요를 누른 회원의 목록을 가져온다.
     */
    @Transactional
    @Test
    public void getFeedLikeListTest() {
        List<FeedLIkeMemberDto> feedLIkeMemberList = feedSubServiceImpl.getFeedLikeList(10000);

        assertThat(feedLIkeMemberList).isNotNull();
        assertThat(feedLIkeMemberList).isNotEmpty();
        assertThat(feedLIkeMemberList.size()).isGreaterThan(0);
        for (FeedLIkeMemberDto feedLIkeMemberDto : feedLIkeMemberList) {
            log.info(feedLIkeMemberDto);
        }
    }

    /* -------------- 3. 피드의 댓글 관련 테스트 -------------- */

    /**
     * 피드의 댓글 리스트를 가져온다.
     */
    @Transactional
    @Test
    public void getFeedCommentListTest() {
        List<FeedCommentDto> feedCommentDtoList = feedSubServiceImpl.getFeedCommentList(10000);

        assertThat(feedCommentDtoList).isNotNull();
        assertThat(feedCommentDtoList).isNotEmpty();
        assertThat(feedCommentDtoList.size()).isGreaterThan(0);

        for (FeedCommentDto feedCommentDto : feedCommentDtoList) {
            log.info(feedCommentDto);
        }
    }

    /**
     * 피드에 댓글을 하나 추가한다.
     */
    @Transactional
    @Test
    public void addFeedCommentTest() {
        FeedCommentDto feedCommentDto = FeedCommentDto.builder()
                .feedId(10000)
                .memberId("member_1")
                .commentText("테스트용 댓글입니다.")
                .writtenTime(LocalDateTime.now())
                .activated(true)
                .build();
        int commentId = feedSubServiceImpl.addFeedComment(feedCommentDto);

        FeedComment feedComment = feedCommentRepository.findByCommentId(commentId);

        log.info(feedComment.getCommentId());
        assertThat(feedComment.getFeed().getFeedId()).isEqualTo(feedCommentDto.getFeedId());
        assertThat(feedComment.getMember().getMemberId()).isEqualTo(feedCommentDto.getMemberId());
        assertThat(feedComment.getCommentText()).isEqualTo(feedCommentDto.getCommentText());
    }

    /**
     * 피드의 댓글을 하나 삭제한다.
     */
    @Transactional
    @Test
    public void deleteFeedCommentTest() {
        int commentId = feedSubServiceImpl.deleteFeedComment(10000);

        FeedComment feedComment = feedCommentRepository.findByCommentId(commentId);

        log.info(feedComment.getCommentId());
        assertThat(feedComment.isActivated()).isFalse();
    }

    /**
     * 피드의 댓글을 수정한다.
     */
    @Transactional
    @Test
    public void updateFeedCommentTest() {
        FeedCommentDto feedCommentDto = FeedCommentDto.builder()
                .commentId(10000)
                .commentText("수정 테스트용 댓글입니다.")
                .build();

        int commentId = feedSubServiceImpl.updateFeedCommnet(feedCommentDto);

        FeedComment feedComment = feedCommentRepository.findByCommentId(commentId);
        assertThat(feedComment.getCommentText()).isEqualTo(feedCommentDto.getCommentText());
    }
    
    /* -------------- 4. 피드의 태그 관련 테스트 -------------- */

    /**
     * 피드에 등록된 태그를 가져온다.
     */
    @Transactional
    @Test
    public void getFeedTagListTest() {
        List<TagDto> tagDtoList = feedSubServiceImpl.getFeedTagList(10000);

        assertThat(tagDtoList).isNotNull();
        assertThat(tagDtoList).isNotEmpty();
        assertThat(tagDtoList.size()).isGreaterThan(0);
        for (TagDto tagDto : tagDtoList) {
            log.info(tagDto);
        }
    }

    /**
     * 피드의 태그를 하나 추가한다.
     */
    @Transactional
    @Test
    public void addFeedTagTest() {
        int feedId = feedSubServiceImpl.addFeedTag(10000, "흠흐밍");

        List<TagDto> tagDtoList = feedSubServiceImpl.getFeedTagList(feedId);

        assertThat(tagDtoList).isNotNull();
        assertThat(tagDtoList).isNotEmpty();
        assertThat(tagDtoList.size()).isGreaterThan(0);

        boolean isAddTag = false;
        for (TagDto tagDto : tagDtoList) {
            if(tagDto.getTagText().equals("흠흐밍")) {
                isAddTag = true;
            }
        }

        assertThat(isAddTag).isTrue();
    }

    /**
     * 피드의 태그를 하나 삭제한다.
     */
    @Transactional
    @Test
    public void deleteFeedTagTest() {
        int result = feedSubServiceImpl.deleteFeedTag(10000, "행복");

        assertThat(result).isEqualTo(1);


    }
}
