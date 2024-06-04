package com.kube.noon.customersupport.service;

import com.kube.noon.customersupport.dto.report.ReportDto;
import com.kube.noon.customersupport.dto.report.ReportProcessingDto;

import java.util.List;

public interface CustomerSupportService {

    List<ReportDto> getReportList();
    ReportDto getReportByReportId(int reportId);
    ReportDto addReport(ReportDto reportDto);
    ReportProcessingDto updateReport(int reportId);

}
