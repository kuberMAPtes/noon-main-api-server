package com.kube.noon.customersupport.repository;

import com.kube.noon.feed.domain.FeedAttachment;
import java.util.List;

public interface AttachmentFilteringRepository {
    public String addBluredFile(String fileUrl);
    public List<FeedAttachment> findBadImageListByAI(List<FeedAttachment> feedAttachmentList);
}
