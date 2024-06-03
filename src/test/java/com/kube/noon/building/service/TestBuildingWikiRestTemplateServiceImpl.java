package com.kube.noon.building.service;

import com.kube.noon.building.service.buildingwiki.BuildingWikiRestTemplateServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class TestBuildingWikiRestTemplateServiceImpl {

    @Autowired
    BuildingWikiRestTemplateServiceImpl wikiService;

    @DisplayName("직접 위키 페이지 확인해 보면서 테스트")
    @Test
    void addPage() {
        wikiService.addPage("Sample-pageasdf");
    }
}