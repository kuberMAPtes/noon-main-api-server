package com.kube.noon.customersupport.repository;

import com.kube.noon.feed.domain.FeedAttachment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface AttachmentFilteringRepository {
    public String addBlurredFile(String fileUrl, int attachmentId);
    public List<FeedAttachment> findBadImageListByAI(List<FeedAttachment> feedAttachmentList);
    public Page<FeedAttachment> findBadImageListByAI(List<FeedAttachment> feedAttachmentList, Pageable pageable);
}
