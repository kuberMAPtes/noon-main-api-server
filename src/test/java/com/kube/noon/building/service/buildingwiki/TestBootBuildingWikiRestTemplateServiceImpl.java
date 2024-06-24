package com.kube.noon.building.service.buildingwiki;

import com.kube.noon.building.domain.Building;
import com.kube.noon.building.dto.wiki.BuildingWikiEditRequestDto;
import com.kube.noon.building.dto.wiki.BuildingWikiPageResponseDto;
import com.kube.noon.building.repository.BuildingProfileRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Transactional
@ActiveProfiles({ "prod", "key", "privpark", "proddddd" })
@SpringBootTest
class TestBootBuildingWikiRestTemplateServiceImpl {

    @Autowired
    private BuildingWikiRestTemplateServiceImpl wikiRestTemplateService;

    @Autowired
    private BuildingProfileRepository buildingProfileRepository;
//
//    Building sampleBuilding;
//
//    @Test
//    void addPage() throws Exception {
//        this.wikiRestTemplateService.addPage("id15000");
//    }
//
//    @DisplayName("위키 페이지에 Id15000인 제목의 페이지가 있어야 함/DB에 건물 id가 15000인 건물이 있어야 함")
//    @Test
//    void getReadPage() {
//        BuildingWikiPageResponseDto readPage = this.wikiRestTemplateService.getReadPage(15000);
//        log.info("buildingName={}", readPage.getBuildingName());
//        log.info("page={}", readPage.getHtmlContent());
//    }
//
    @Test
    void getEditPage() {
        BuildingWikiPageResponseDto editPage = this.wikiRestTemplateService.getEditPage(15000);
        log.info("buildingName={}", editPage.getBuildingName());
        log.info("page={}", editPage.getHtmlContent());
    }
//
//    @Test
//    void editPage() {
//        BuildingWikiEditRequestDto requestDto = new BuildingWikiEditRequestDto();
//        requestDto.setBuildingId(15000);
//        requestDto.setWpUnicodeCheck("ℳ\uD835\uDCB2♥\uD835\uDCCA\uD835\uDCC3\uD835\uDCBE\uD835\uDCB8ℴ\uD835\uDCB9ℯ");
//        requestDto.setWpAntispam("");
//        requestDto.setWikieditorUsed("wikieditorUsed");
//        requestDto.setWpSection("wpSection");
//        requestDto.setWpStarttime("20240624012434");
//        requestDto.setWpEdittime("20240624002457");
//        requestDto.setEditRevId("10");
//        requestDto.setWpScrolltop("wpScrolltop");
//        requestDto.setParentRevId("10");
//        requestDto.setFormat("text/x-wiki");
//        requestDto.setModel("wikitext");
//        requestDto.setWpTextbox1("ModifiedModified");
//        requestDto.setWpSummary("Sample summary");
//        requestDto.setWpEditToken("+\\");
//        requestDto.setMode("text");
//        requestDto.setWpUltimateParam("1");
//        requestDto.setWpAutoSummary("d41d8cd98f00b204e9800998ecf8427e");
//        requestDto.setOldid("0");
//
//        this.wikiRestTemplateService.editPage(requestDto);
//    }
}
