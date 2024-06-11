package com.kube.noon.customersupport.service;
import com.kube.noon.common.FileType;
import com.kube.noon.customersupport.domain.Report;
import com.kube.noon.customersupport.dto.notice.NoticeDto;
import com.kube.noon.customersupport.dto.report.ReportDto;
import com.kube.noon.customersupport.dto.report.ReportProcessingDto;
import com.kube.noon.feed.domain.Feed;
import com.kube.noon.feed.domain.FeedAttachment;
import com.kube.noon.feed.dto.FeedAttachmentDto;
import com.kube.noon.member.service.MemberService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import lombok.extern.slf4j.Slf4j;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
@SpringBootTest
public class TestCustomerSupportService {

    @Autowired
    private CustomerSupportService customerSupportService;

    @Autowired
    private MemberService memberService;


    @Test
    void getNoticeList(){
        List<NoticeDto> noticeDtoList = customerSupportService.getNoticeList();
        log.info(noticeDtoList.toString());
    }


    @Test
    void getNoticeListByPageable(){
        List<NoticeDto> noticeDtoList = customerSupportService.getNoticeListByPageable(0);
        log.info(noticeDtoList.toString());
    }


    @Test
    void getLatestReport(){
        ReportDto report = customerSupportService.getLatestReport();
        log.info(report.toString());
    }


    @Test
    void getReportList(){

        List<ReportDto> reportDtoList = customerSupportService.getReportList();
        log.info(reportDtoList.toString());

    }

    @Test
    void getReportListByPageable(){

        List<ReportDto> reportDtoList = customerSupportService.getReportListByPageable(0);
        log.info(reportDtoList.toString());

    }


    @Test
    void getReportByReportId(){

        ReportDto reportDto = customerSupportService.getReportByReportId(10000);

        assertThat(reportDto).isNotNull();
        log.info(reportDto.toString());

    }


    @Test
    void addReport(){

        ReportDto reportDto = ReportDto.builder()
                .reporterId("member_757")
                .reporteeId("member_1826")
                .reportStatus(Report.ReportStatus.PEND)
                .reportText("얘가 울엄마 욕함")
                .reportedTime(LocalDateTime.now())
                .build();

        ReportDto addedReportDto = this.customerSupportService.addReport(reportDto);

        assertThat(addedReportDto)
                .usingRecursiveComparison()
                .ignoringFields("reportId")
                .isEqualTo(reportDto);

    }

    /**
     * 수정한 신고 상태, 신고처리 텍스트가 변경된 reportDto를 통해 신고를 update하고
     * 입력받은 계정잠금연장일수로 피신고자의 잠금일수를 연장(MemberService사용)
     *
     */
    @Test
    void updateReport() {

        ReportDto reportDto = customerSupportService.getReportByReportId(10002);
        log.info(reportDto.toString());

        ReportProcessingDto reportProcessingDto = new ReportProcessingDto();
        BeanUtils.copyProperties(reportDto, reportProcessingDto);
        reportProcessingDto.setReportStatus(Report.ReportStatus.ACCEPT);
        reportProcessingDto.setUnlockDuration("SEVEN_DAYS");
        reportProcessingDto.setProcessingText(reportDto.getReporterId()+"님이 접수한"+reportDto.getReporteeId()+"님에 대한 신고가 승인되었습니다.");

        log.info(customerSupportService.updateReport(reportProcessingDto).toString());

    }


    @Test
    void addBluredImage(){

        Feed feed = Feed.builder().feedId(10000).build();
        FeedAttachment feedAttachment = FeedAttachment.builder()
                .feed(feed)
                .fileUrl("https://kr.object.ncloudstorage.com/noon-images/KakaoTalk_20240606_141731630.jpg")
                .fileType(FileType.PHOTO)
                .blurredFileUrl(null)
                .activated(true)
                .build();


        try {
            customerSupportService.addBluredImage(FeedAttachmentDto.toDto(feedAttachment));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        
    }

    @Test
    void getFilteredListByAI(){
        List<FeedAttachmentDto> filteredList = customerSupportService.getFilteredListByAI();
        log.info("유해성 1차 필터링된 첨부파일={}",filteredList);
    }

    @Test
    void getFilteredListByAIAndPageable(){
        List<FeedAttachmentDto> filteredList = customerSupportService.getFilteredListByAIAndPageable(0);
        log.info("유해성 1차 필터링된 첨부파일 첫 페이지={}",filteredList);
    }
}
