package com.kube.noon.customersupport.service;

import com.kube.noon.customersupport.dto.notice.NoticeDto;
import com.kube.noon.customersupport.dto.report.ReportDto;
import com.kube.noon.customersupport.dto.report.ReportProcessingDto;
import com.kube.noon.feed.dto.FeedAttachmentDto;
import com.kube.noon.feed.dto.FeedDto;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface CustomerSupportService {

    String addFile(MultipartFile attachment);
    FeedDto addNotice(String wirterId, String title, String text) throws IOException;
    List<NoticeDto> getNoticeList();
    List<NoticeDto> getNoticeListByPageable(int pageNumber);

    ReportDto getLatestReport();
    List<ReportDto> getReportList();
    List<ReportDto> getReportListByPageable(int pageNumber);
    ReportDto getReportByReportId(int reportId);
    ReportDto addReport(ReportDto reportDto);
    ReportProcessingDto updateReport(ReportProcessingDto reportProcessingDto);

    void updateUnlockTime(String memberId, String reqUnlockDuration);
    List<FeedAttachmentDto> getImageList();
    List<FeedAttachmentDto> getImageListByPageable(int pageNumber);
    List<FeedAttachmentDto> getAllImageListByPageable(int pageNumber);
    List<FeedAttachmentDto> getAllImageList();

    FeedAttachmentDto getImageByAttatchmentId(int attachmentId);
    FeedAttachmentDto addBluredImage(FeedAttachmentDto attachmentDto) throws IOException;
    FeedDto deleteBadFeed(FeedDto feedDto, String reqUnlockDuration);
    List<FeedAttachmentDto> getFilteredListByAI();
    List<FeedAttachmentDto> getFilteredListByAIAndPageable(int pageNumber);

    void sendReportNotification(ReportProcessingDto reportProcessingDto);
}
