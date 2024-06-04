package com.kube.noon.customersupport.service;

import com.kube.noon.building.dto.BuildingDto;
import com.kube.noon.customersupport.domain.Report;
import com.kube.noon.customersupport.dto.report.ReportDto;
import com.kube.noon.customersupport.dto.report.ReportProcessingDto;
import com.kube.noon.customersupport.repository.ReportRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 신고, 공지사항, 유해사진필터링 서비스를 제공하는 구현체이다.
 *
 * @author 허예지
 */
@Service
@RequiredArgsConstructor
public class CustomerSupportServiceImpl implements CustomerSupportService{

    private final ReportRepository reportRepository;


    /**
     * 신고 목록을 조회
     *
     * @return 신고 목록 Dto
     */
    @Override
    public List<ReportDto> getReportList() {

        List<Report> reports = reportRepository.findAll();

        return reports.stream()
                .map(ReportDto::fromEntity)
                .collect(Collectors.toList());
    }




    /**
     * 신고 아이디를 통해 특정 신고의 내용을 조회한다.
     *
     * @param reportId 신고아이디
     * @return 조회한 신고 Dto
     */
    @Override
    public ReportDto getReportByReportId(int reportId) {
        return ReportDto.fromEntity(reportRepository.findReportByReportId(reportId));
    }




    /**
     * ReportDto로 입력받은 신고를 신고테이블에 추가한다.
     *
     * @param reportDto 신고 내용을 담은 신고 Dto
     * @return 신고테이블에서 가장 최근에 추가된 신고(내가 추가한 신고) Dto
     */
    @Transactional
    @Override
    public ReportDto addReport(ReportDto reportDto) {

        reportRepository.save(reportDto.toEntity());

        return ReportDto.fromEntity(reportRepository.findLatestReport());
    }


    /**
     * reportId로 조회한 신고를 '처리'한다.
     * 신고 처리에 관한 설명은 NOON 참조 문서 [참조 25]
     *
     * @param reportId
     * @return
     */
    @Transactional
    @Override
    public ReportProcessingDto updateReport(int reportId) {
        return null;
    }
}
