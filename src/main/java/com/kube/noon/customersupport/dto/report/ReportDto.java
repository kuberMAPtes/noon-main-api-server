package com.kube.noon.customersupport.dto.report;

import com.kube.noon.customersupport.domain.Report;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class ReportDto {
    private int reportId;
    private String reporterId;
    private String reporteeId;
    private Report.ReportStatus reportStatus;
    private String reportText;
    private LocalDateTime reportedTime;


    public static ReportDto fromEntity(Report report) {
        return ReportDto.builder()
                .reportId(report.getReportId())
                .reporterId(report.getReporterId())
                .reporteeId(report.getReporteeId())
                .reportStatus(report.getReportStatus())
                .reportText(report.getReportText())
                .reportedTime(report.getReportedTime())
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
                .build();
    }

}
