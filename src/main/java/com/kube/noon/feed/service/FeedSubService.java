package com.kube.noon.feed.service;

import com.kube.noon.common.FileType;
import com.kube.noon.feed.dto.FeedAttachmentDto;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface FeedSubService {
    public List<FeedAttachmentDto> getFeedAttachementListByFileType(FileType fileType);
}
