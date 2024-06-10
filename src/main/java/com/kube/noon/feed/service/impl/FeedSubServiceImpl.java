package com.kube.noon.feed.service.impl;

import com.kube.noon.common.FileType;
import com.kube.noon.common.zzim.Zzim;
import com.kube.noon.common.zzim.ZzimRepository;
import com.kube.noon.common.zzim.ZzimType;
import com.kube.noon.feed.domain.*;
import com.kube.noon.feed.dto.FeedAttachmentDto;
import com.kube.noon.feed.dto.FeedCommentDto;
import com.kube.noon.feed.dto.FeedLIkeMemberDto;
import com.kube.noon.feed.dto.TagDto;
import com.kube.noon.feed.repository.*;
import com.kube.noon.feed.service.FeedSubService;
import com.kube.noon.member.domain.Member;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Log4j2
@RequiredArgsConstructor
public class FeedSubServiceImpl implements FeedSubService {

    private final FeedRepository feedRepository;
    private final FeedAttachmentRepository feedAttachmentRepository;
    private final FeedCommentRepository feedCommentRepository;
    private final TagFeedRepository tagFeedRepository;
    private final TagRepository tagRepository;
    private final ZzimRepository zzimRepository;


    @Override
    public List<FeedAttachmentDto> getFeedAttachementListByFileType(FileType fileType) {
        List<FeedAttachment> feedAttachments = feedAttachmentRepository.findByFileType(fileType);

        return FeedAttachmentDto.toDtoList(feedAttachments);
    }

    @Override
    public FeedAttachmentDto getFeedAttachment(int attachmentId) {
        return FeedAttachmentDto.toDto(feedAttachmentRepository.findByAttachmentId(attachmentId));
    }

    @Override
    public List<FeedAttachmentDto> getFeedAttachmentList(int feedId) {
        List<FeedAttachment> feedAttachmentList = feedAttachmentRepository.findByFeedAndActivatedTrue(Feed.builder().feedId(feedId).build());

        return FeedAttachmentDto.toDtoList(feedAttachmentList);
    }

    @Transactional
    @Override
    public int addFeedAttachment(FeedAttachmentDto feedAttachmentDto) {
        FeedAttachment addFeedAttachment = FeedAttachmentDto.toEntity(feedAttachmentDto);

        return feedAttachmentRepository.save(addFeedAttachment).getAttachmentId();
    }

    @Transactional
    @Override
    public int deleteFeedAttachment(int attachmentId) {
        FeedAttachment deleteFeedAttachment = feedAttachmentRepository.findByAttachmentId(attachmentId);

        deleteFeedAttachment.setActivated(false);

        return feedAttachmentRepository.save(deleteFeedAttachment).getAttachmentId();
    }

    @Transactional
    @Override
    public int addFeedLike(int feedId, String memberId) {
        // 1) 좋아요 데이터가 있는지 확인한다.
        Zzim zzimLike = zzimRepository.findByFeedIdAndMemberIdAndZzimType(feedId, memberId, ZzimType.LIKE);
        Feed feed = feedRepository.findByFeedId(feedId);
        Zzim resultZzim;

        if(zzimLike == null) { // 2) 없다면, 하나 추가한다.
            Zzim newZzimLike = Zzim.builder()
                    .memberId(memberId)
                    .feedId(feedId)
                    .zzimType(ZzimType.LIKE)
                    .buildingId(feed.getBuilding().getBuildingId())
                    .subscriptionProviderId(null)
                    .activated(true)
                    .build();
            resultZzim = zzimRepository.save(newZzimLike);
        } else { // 3) 있다면. activated = true로 설정한다.
            zzimLike.setActivated(true);
            resultZzim = zzimRepository.save(zzimLike);
        }

        return resultZzim.getZzimId();
    }

    @Transactional
    @Override
    public int deleteFeedLike(int feedId, String memberId) {
        Zzim deleteZzim = zzimRepository.findByFeedIdAndMemberIdAndZzimType(feedId, memberId, ZzimType.LIKE);

        if(deleteZzim != null) {
            deleteZzim.setActivated(false);
            return zzimRepository.save(deleteZzim).getZzimId();
        } else {
            return -1; // error
        }
    }

    @Transactional
    @Override
    public int addFeedBookmark(int feedId, String memberId) {
        // 1) 북마크 데이터가 있는지 확인한다.
        Zzim zzimLike = zzimRepository.findByFeedIdAndMemberIdAndZzimType(feedId, memberId, ZzimType.BOOKMARK);
        Feed feed = feedRepository.findByFeedId(feedId);
        Zzim resultZzim;

        if(zzimLike == null) { // 2) 없다면, 하나 추가한다.
            Zzim newZzimBookmark = Zzim.builder()
                    .memberId(memberId)
                    .feedId(feedId)
                    .zzimType(ZzimType.BOOKMARK)
                    .buildingId(feed.getBuilding().getBuildingId())
                    .subscriptionProviderId(null)
                    .activated(true)
                    .build();
            resultZzim = zzimRepository.save(newZzimBookmark);
        } else { // 3) 있다면. activated = true로 설정한다.
            zzimLike.setActivated(true);
            resultZzim = zzimRepository.save(zzimLike);
        }

        return resultZzim.getZzimId();
    }

    @Transactional
    @Override
    public int deleteFeedBookmark(int feedId, String memberId) {
        Zzim deleteZzim = zzimRepository.findByFeedIdAndMemberIdAndZzimType(feedId, memberId, ZzimType.BOOKMARK);

        if(deleteZzim != null) {
            deleteZzim.setActivated(false);
            return zzimRepository.save(deleteZzim).getZzimId();
        } else {
            return -1; // error
        }
    }

    @Override
    public List<FeedLIkeMemberDto> getFeedLikeList(int feedId) {
        List<Member> feedLikeMemberList = feedRepository.getFeedLikeList(Feed.builder().feedId(feedId).build());

        return FeedLIkeMemberDto.toDtoList(feedLikeMemberList);
    }

    @Override
    public List<FeedCommentDto> getFeedCommentList(int feedId) {
        List<FeedComment> feedCommentList = feedCommentRepository.findByFeed(Feed.builder().feedId(feedId).build());

        return FeedCommentDto.toDtoList(feedCommentList);
    }

    @Override
    public int addFeedComment(FeedCommentDto feedCommentDto) {
        FeedComment addFeedComment = FeedCommentDto.toEntity(feedCommentDto);

        return feedCommentRepository.save(addFeedComment).getCommentId();
    }

    @Override
    public int deleteFeedComment(int commentId) {
        FeedComment deleteFeedComment = feedCommentRepository.findByCommentId(commentId);

        if(deleteFeedComment != null) {
            deleteFeedComment.setActivated(false);
            return feedCommentRepository.save(deleteFeedComment).getCommentId();
        } else {
            return -1;
        }
    }

    @Override
    public int updateFeedCommnet(FeedCommentDto feedCommentDto) {
        FeedComment updateFeedComment = feedCommentRepository.findByCommentId(feedCommentDto.getCommentId());

        if(updateFeedComment != null) {
            updateFeedComment.setCommentText(feedCommentDto.getCommentText());
            return feedCommentRepository.save(updateFeedComment).getCommentId();
        } else {
            return -1;
        }
    }

    @Override
    public List<TagDto> getFeedTagList(int feedId) {
        List<Tag> getTagByFeedId = tagRepository.getTagByFeedId(Feed.builder().feedId(feedId).build());

        return TagDto.toDtoList(getTagByFeedId);
    }

    @Override
    public int addFeedTag(int feedId, String tagText) {

        // 1. 삽입 전 tag 테이블에 존재하는지 탐색
        Tag tag = tagRepository.findByTagText(tagText);

        // 1-1. 만약 테이블에 없다면 삽입하기
        if(tag == null) {
            tag = Tag.builder().tagText(tagText).build();
            tagRepository.save(tag);
        }

        // 2. tag_feed 테이블에 추가하기
        Feed feed = Feed.builder().feedId(feedId).build();
        TagFeed tagFeed = TagFeed.builder().feed(feed).tag(tag).build();

        return tagFeedRepository.save(tagFeed).getFeed().getFeedId();
    }

    @Override
    public int deleteFeedTag(int feedId, String tagText) {
        // 1. 태그 텍스트에 대한 번호를 가져온다.
        Tag tag = tagRepository.findByTagText(tagText);

        // 1-1. 만약 없으면 종료
        if(tag == null) {
            return -1;
        }

        // 2. 태그를 지운다.
        return tagFeedRepository.deleteByTagAndFeed(tag, Feed.builder().feedId(feedId).build());
    }
}
