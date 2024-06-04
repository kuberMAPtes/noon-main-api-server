package com.kube.noon.customersupport.domain;

import com.kube.noon.member.domain.Member;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "report")
public class Report {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "report_id")
    private Integer reportId;

    @Column(name = "reporter_id", nullable = false, length = 20)
    private String reporterId;

    @Column(name = "reportee_id", nullable = false, length = 20)
    private String reporteeId;

    @Enumerated(EnumType.STRING)
    @Column(name = "report_status", nullable = false)
    private ReportStatus reportStatus;

    @Column(name = "report_text", nullable = false, length = 1000)
    private String reportText;

    @Column(name = "reported_time", nullable = false, columnDefinition = "DATETIME DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime reportedTime;

    @Column(name = "processing_text", length = 1000)
    private String processingText;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reporter_id", insertable = false, updatable = false)
    private Member reporter;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reportee_id", insertable = false, updatable = false)
    private Member reportee;

    public enum ReportStatus {
        PEND,
        ACCEPT,
        REJECT
    }
}