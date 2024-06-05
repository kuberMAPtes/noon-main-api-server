package com.kube.noon.feed.domain;

import com.kube.noon.common.FileType;
import jakarta.persistence.*;
import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "feed_attachment")
public class FeedAttachment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "attachment_id")
    private int attachmentId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "feed_id", nullable = false)
    private Feed feed;

    @Column(name = "file_url", nullable = false, columnDefinition = "TEXT")
    private String fileUrl;

    @Enumerated(EnumType.STRING)
    @Column(name = "file_type", nullable = false)
    private FileType fileType;

    @Column(name = "blurred_file_url", length = 15000)
    private String blurredFileUrl;

    @Column(name = "activated", nullable = false)
    private boolean activated = true;

    public void setActivated(boolean activated) {
        this.activated = activated;
    }

    @Override
    public String toString() {
        return "attachmentId : " + attachmentId + " / fileUrl : " + fileUrl + " / activated : " + activated + " / FileType : " + fileType;
    }
}
