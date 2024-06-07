package com.kube.noon.customersupport.service;

import com.kube.noon.customersupport.dto.report.ReportDto;
import com.kube.noon.customersupport.dto.report.ReportProcessingDto;
import com.kube.noon.feed.dto.FeedAttachmentDto;
import java.io.IOException;
import java.util.List;

public interface CustomerSupportService {

    List<ReportDto> getReportList();
    ReportDto getReportByReportId(int reportId);
    ReportDto addReport(ReportDto reportDto);
    ReportProcessingDto updateReport(ReportProcessingDto reportProcessingDto, String unlockDuration);

    FeedAttachmentDto addBluredImage(FeedAttachmentDto attachmentDto) throws IOException;
}
