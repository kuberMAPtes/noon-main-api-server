package com.kube.noon.feed.service.impl;

import com.kube.noon.common.FileType;
import com.kube.noon.feed.domain.FeedAttachment;
import com.kube.noon.feed.dto.FeedAttachmentDto;
import com.kube.noon.feed.repository.*;
import com.kube.noon.feed.service.FeedSubService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

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


    @Override
    public List<FeedAttachmentDto> getFeedAttachementListByFileType(FileType fileType) {
        List<FeedAttachment> feedAttachments = feedAttachmentRepository.findByFileType(fileType);

        return FeedAttachmentDto.toDtoList(feedAttachments);
    }
}
