package com.kube.noon.feed.dto;

import com.kube.noon.common.FileType;
import com.kube.noon.feed.domain.Feed;
import com.kube.noon.feed.domain.FeedAttachment;
import lombok.*;

import java.util.List;
import java.util.stream.Collectors;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class FeedAttachmentDto {
    private int attachmentId;
    private int feedId;
    private String fileUrl;
    private FileType fileType;
    private String blurredFileUrl;
    private boolean activated;

    public static FeedAttachmentDto toDto(FeedAttachment feedAttachment) {
        if(feedAttachment == null) return null;

        return FeedAttachmentDto.builder()
                .attachmentId(feedAttachment.getAttachmentId())
                .feedId(feedAttachment.getFeed().getFeedId())
                .fileUrl(feedAttachment.getFileUrl())
                .fileType(feedAttachment.getFileType())
                .blurredFileUrl(feedAttachment.getBlurredFileUrl())
                .activated(feedAttachment.isActivated())
                .build();
    }

    public static FeedAttachment toEntity(FeedAttachmentDto feedAttachmentDto) {
        return FeedAttachment.builder()
                .attachmentId(feedAttachmentDto.getAttachmentId())
                .feed(Feed.builder().feedId(feedAttachmentDto.getFeedId()).build())
                .fileUrl(feedAttachmentDto.getFileUrl())
                .fileType(feedAttachmentDto.getFileType())
                .blurredFileUrl(feedAttachmentDto.getBlurredFileUrl())
                .activated(feedAttachmentDto.isActivated())
                .build();
    }

    public static List<FeedAttachmentDto> toDtoList(List<FeedAttachment> attachments) {
        return attachments.stream().map(FeedAttachmentDto::toDto).collect(Collectors.toList());
    }
}
