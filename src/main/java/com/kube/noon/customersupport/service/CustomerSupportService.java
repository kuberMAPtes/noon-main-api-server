package com.kube.noon.customersupport.service;

import com.kube.noon.customersupport.domain.Report;
import com.kube.noon.customersupport.dto.notice.NoticeDto;
import com.kube.noon.customersupport.dto.report.ReportDto;
import com.kube.noon.customersupport.dto.report.ReportProcessingDto;
import com.kube.noon.feed.dto.FeedAttachmentDto;
import java.io.IOException;
import java.util.List;

public interface CustomerSupportService {

    List<NoticeDto> getNoticeList();
    List<NoticeDto> getNoticeListByPageable(int pageNumber);

    ReportDto getLatestReport();
    List<ReportDto> getReportList();
    List<ReportDto> getReportListByPageable(int pageNumber);
    ReportDto getReportByReportId(int reportId);
    ReportDto addReport(ReportDto reportDto);
    ReportProcessingDto updateReport(ReportProcessingDto reportProcessingDto);

    FeedAttachmentDto getImageByAttatchmentId(int attachmentId);
    FeedAttachmentDto addBluredImage(FeedAttachmentDto attachmentDto) throws IOException;
    List<FeedAttachmentDto> getFilteredListByAI();
    List<FeedAttachmentDto> getFilteredListByAIAndPageable(int pageNumber);


}
