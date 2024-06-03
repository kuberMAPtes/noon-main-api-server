package com.kube.noon.customersupport.domain;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.Date;

@Data
public class Report {
    private int reportId;
    private String reporterId;
    private String reporteeId;
    private int reportStatus;
    private String reportText;
    private Date reportedTime;
    private String processingText;
}
