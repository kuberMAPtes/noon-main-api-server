package com.kube.noon.feed.dto;

import com.kube.noon.common.FileType;
import com.kube.noon.feed.domain.Feed;
import lombok.*;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class FeedAttachmentDto {
    private int attachmentId;
    private Feed feed;
    private String fileUrl;
    private FileType fileType;
    private String blurredFileUrl;
    private boolean activated;
}
