package com.kube.noon.customersupport.dto.report;

import com.kube.noon.customersupport.domain.Report;
import com.kube.noon.member.domain.Member;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * 신고 처리 내용을 담는 Dto
 *
 */
@ToString
@NoArgsConstructor
@Getter
@Setter
@AllArgsConstructor
@Builder
public class ReportProcessingDto {

    private Integer reportId;
    private String reporterId;
    private String reporteeId;
    private Report.ReportStatus reportStatus;
    private String reportText;
    private LocalDateTime reportedTime;
    private String processingText;
    private String unlockDuration;


    public static ReportProcessingDto fromEntity(Report report) {
        return ReportProcessingDto.builder()
                .reportId(report.getReportId())
                .reporterId(report.getReporterId())
                .reporteeId(report.getReporteeId())
                .reportStatus(report.getReportStatus())
                .reportText(report.getReportText())
                .reportedTime(report.getReportedTime())
                .processingText(report.getProcessingText())
                .build();
    }

    public Report toEntity() {
        return Report.builder()
                .reportId(this.reportId)
                .reporterId(this.reporterId)
                .reporteeId(this.reporteeId)
                .reportStatus(this.reportStatus)
                .reportText(this.reportText)
                .reportedTime(this.reportedTime)
                .processingText(this.processingText)
                .build();
    }


}
