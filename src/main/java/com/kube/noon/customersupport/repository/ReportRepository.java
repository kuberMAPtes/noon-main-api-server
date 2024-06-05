package com.kube.noon.customersupport.repository;

import com.kube.noon.customersupport.domain.Report;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;


@Repository
public interface ReportRepository extends JpaRepository<Report, Integer> {

    //findReportList => findAll()
    //updateReport => save()
    //addReport => save()

    @Query(value = "SELECT * FROM report ORDER BY report_id DESC LIMIT 1", nativeQuery = true)
    Report findLatestReport();

    Report findReportByReportId(int reportId);

}
