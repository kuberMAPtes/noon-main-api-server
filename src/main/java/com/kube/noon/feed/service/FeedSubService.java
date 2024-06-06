package com.kube.noon.feed.service;

import com.kube.noon.common.FileType;
import com.kube.noon.feed.dto.FeedAttachmentDto;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface FeedSubService {
    // FeedAttactmentDto : 피드 첨부파일 관련
    public List<FeedAttachmentDto> getFeedAttachementListByFileType(FileType fileType);

    public FeedAttachmentDto getFeedAttachment(int attachmentId);

    public List<FeedAttachmentDto> getFeedAttachmentList(int feedId);

    public int addFeedAttachment(FeedAttachmentDto feedAttachmentDto);

    public int deleteFeedAttachment(int attachmentId);
}
