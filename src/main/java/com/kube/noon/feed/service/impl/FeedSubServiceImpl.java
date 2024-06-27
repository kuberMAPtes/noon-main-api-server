package com.kube.noon.feed.service.impl;

import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.kube.noon.common.FileType;
import com.kube.noon.common.ObjectStorageAPI;
import com.kube.noon.common.ObjectStorageAPIProfile;
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
import com.kube.noon.member.repository.MemberJpaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
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
    private final ObjectStorageAPI objectStorageAPI;
    private final MemberJpaRepository memberJpaRepository;
    private final ObjectStorageAPIProfile objectStorageAPIProfile; // test

    @Override
    public List<FeedAttachmentDto> getFeedAttachementListByFileType(FileType fileType) {
        List<FeedAttachment> feedAttachments = feedAttachmentRepository.findByFileType(fileType);

        return FeedAttachmentDto.toDtoList(feedAttachments);
    }

    @Override
    public ResponseEntity<byte[]> getFeedAttachment(int attachmentId) {
        FeedAttachmentDto feedAttachmentDto = FeedAttachmentDto.toDto(feedAttachmentRepository.findByAttachmentId(attachmentId));

        if (feedAttachmentDto == null) {
            // return new ResponseEntity<>(HttpStatus.NOT_FOUND); // 찾을 수 없음을 확인(404)
            return null;
        }

        String[] fileNames = feedAttachmentDto.getFileUrl().split("/");
        String fileName = fileNames[fileNames.length - 1];


        S3ObjectInputStream inputStream = objectStorageAPI.getObject(fileName);

        if (inputStream == null) {
            // return new ResponseEntity<>(HttpStatus.NOT_FOUND); // 찾을 수 없음을 확인(404)
            return null;
        }

        try {
            byte[] imageBytes = inputStream.readAllBytes();
            HttpHeaders headers = new HttpHeaders();

            MediaType mediaType = getMediaType(fileName);
            headers.setContentType(mediaType);

            return new ResponseEntity<>(imageBytes, headers, HttpStatus.OK);
        } catch(IOException e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // 파일 확장자를 기반으로 MIME 타입을 반환하는 메소드
    private MediaType getMediaType(String fileName) {
        String[] parts = fileName.split("\\.");
        String extension = parts[parts.length - 1].toLowerCase(); // 파일 확장자를 소문자로 변환하여 비교

        switch (extension) {
            case "jpg": case "jpeg":
            case "png": case "gif":
                return MediaType.IMAGE_JPEG; // 이미지 파일인 경우
            case "mp4":
                return MediaType.valueOf("video/mp4"); // 동영상 파일인 경우
            default:
                return MediaType.APPLICATION_OCTET_STREAM; // 기타 파일은 바이너리 스트림으로 처리
        }
    }

    @Override
    public List<FeedAttachmentDto> getFeedAttachmentList(int feedId) {
        List<FeedAttachment> feedAttachmentList = feedAttachmentRepository.findByFeedAndActivatedTrue(Feed.builder().feedId(feedId).build());

        return FeedAttachmentDto.toDtoList(feedAttachmentList);
    }

    @Transactional
    @Override
    public int addFeedAttachment(int feedId, List<MultipartFile> multipartFileList) {

        // 첨부파일은 Object Storage에 넣는다.
        try {
            if(multipartFileList != null && multipartFileList.size() > 0) {
                for(MultipartFile file : multipartFileList) {
                    String originalFileName = file.getOriginalFilename();

                    // Object Storage에 넣기
                    String Url = objectStorageAPI.putObject(originalFileName, file);
                    // String Url = objectStorageAPIProfile.putObject(originalFileName, file); // test

                    // DB에 넣기
                    feedAttachmentRepository.save(FeedAttachment.builder()
                            .feed(Feed.builder().feedId(feedId).build())
                            .fileUrl(Url)
                            .fileType(FileType.PHOTO) // 임시
                            .blurredFileUrl(null)
                            .activated(true)
                            .build());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return feedId;
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
        List<Zzim> zzimLikeList = zzimRepository.findByFeedIdAndMemberIdAndZzimTypeOrderByZzimId(feedId, memberId, ZzimType.LIKE);
        Zzim zzimLike = (zzimLikeList.isEmpty() ? null : zzimLikeList.get(0)); // null로 데이터가 존재하는지 아닌지 확인, 중복 데이터 대비하여 List 중 하나를 가지고 옴
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
        List<Zzim> zzimLikeList = zzimRepository.findByFeedIdAndMemberIdAndZzimTypeOrderByZzimId(feedId, memberId, ZzimType.LIKE);
        Zzim deleteZzim = (zzimLikeList.isEmpty() ? null : zzimLikeList.get(0));

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
        List<Zzim> zzimBookmarkList = zzimRepository.findByFeedIdAndMemberIdAndZzimTypeOrderByZzimId(feedId, memberId, ZzimType.BOOKMARK);
        Zzim zzimBookmark = (zzimBookmarkList.isEmpty() ? null : zzimBookmarkList.get(0));
        Feed feed = feedRepository.findByFeedId(feedId);
        Zzim resultZzim;

        if(zzimBookmark == null) { // 2) 없다면, 하나 추가한다.
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
            zzimBookmark.setActivated(true);
            resultZzim = zzimRepository.save(zzimBookmark);
        }

        return resultZzim.getZzimId();
    }

    @Transactional
    @Override
    public int deleteFeedBookmark(int feedId, String memberId) {
        List<Zzim> zzimBookmarkList = zzimRepository.findByFeedIdAndMemberIdAndZzimTypeOrderByZzimId(feedId, memberId, ZzimType.BOOKMARK);
        Zzim deleteZzim = (zzimBookmarkList.isEmpty() ? null : zzimBookmarkList.get(0));

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
    public FeedCommentDto addFeedComment(FeedCommentDto feedCommentDto) {
        FeedComment addFeedComment = FeedCommentDto.toEntity(feedCommentDto);
        addFeedComment.setActivated(true);

        Member member = memberJpaRepository.findById(feedCommentDto.getMemberId())
                .orElseThrow(() -> new RuntimeException("Member not found"));
        addFeedComment.setMember(member);

        FeedComment savedFeedComment = feedCommentRepository.save(addFeedComment);
        
        return FeedCommentDto.toDto(feedCommentRepository.save(savedFeedComment));
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
