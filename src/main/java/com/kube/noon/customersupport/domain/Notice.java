package com.kube.noon.customersupport.domain;

import lombok.Data;

import java.util.Date;

@Data
public class Notice {
    private String noticeTitle;
    private int noticeId;
    private Date noticeRegDate;
    private String noticeText;
}
