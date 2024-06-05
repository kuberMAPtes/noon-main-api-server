package com.kube.noon.customersupport.service;
import com.kube.noon.customersupport.domain.Report;
import com.kube.noon.customersupport.dto.report.ReportDto;
import com.kube.noon.customersupport.dto.report.ReportProcessingDto;
import com.kube.noon.customersupport.enums.UnlockDuration;
import com.kube.noon.member.domain.Member;
import com.kube.noon.member.dto.UpdateMemberDto;
import com.kube.noon.member.repository.MemberJpaRepository;
import com.kube.noon.member.service.MemberService;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import lombok.extern.slf4j.Slf4j;
import lombok.extern.slf4j.Slf4j;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
@SpringBootTest
public class TestCustomerSupportService {

    @Autowired
    private CustomerSupportService customerSupportService;

    @Autowired
    private MemberService memberService;




    @Test
    void testGetReportList(){

        List<ReportDto> reportDtoList = customerSupportService.getReportList();
        log.info(reportDtoList.toString());

    }


    @Test
    void testGetReportByReportId(){

        ReportDto reportDto = customerSupportService.getReportByReportId(10000);

        assertThat(reportDto).isNotNull();
        log.info(reportDto.toString());

    }

    @Test
    void testAddReport(){

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
    void testUpdateReport() {

        ReportDto reportDto = customerSupportService.getReportByReportId(10002);
        log.info(reportDto.toString());

        ReportProcessingDto reportProcessingDto = new ReportProcessingDto();
        BeanUtils.copyProperties(reportDto, reportProcessingDto);
        reportProcessingDto.setReportStatus(Report.ReportStatus.ACCEPT);
        reportProcessingDto.setProcessingText(reportDto.getReporterId()+"님이 접수한"+reportDto.getReporteeId()+"님에 대한 신고가 승인되었습니다.");

        log.info(customerSupportService.updateReport(reportProcessingDto, "SEVEN_DAYS").toString());

    }
}
