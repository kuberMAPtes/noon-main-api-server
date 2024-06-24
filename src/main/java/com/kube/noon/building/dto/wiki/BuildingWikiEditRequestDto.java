package com.kube.noon.building.dto.wiki;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@NoArgsConstructor
@Getter
@Setter
@ToString
public class BuildingWikiEditRequestDto {
    private int buildingId;
    private String wpTextbox1;
    private String wpUnicodeCheck;
    private String wpAntispam;
    private String wikieditorUsed;
    private String wpSection;
    private String wpStarttime;
    private String wpEdittime;
    private String editRevId;
    private String wpScrolltop;
    private String wpAutoSummary;
    private String oldid;
    private String parentRevId;
    private String format;
    private String model;
    private String wpSummary;
    private String wpEditToken;
    private String mode;
    private String wpUltimateParam;
}
