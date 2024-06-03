package com.kube.noon.building.service;

import com.kube.noon.building.service.buildingwiki.BuildingWikiRestTemplateServiceImpl;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

class TestBuildingWikiRestTemplateServiceImpl {

    BuildingWikiRestTemplateServiceImpl wikiService;

    static Properties properties = new Properties();

    @BeforeAll
    static void beforeAll() throws IOException {
        try (InputStream inputStream = TestBuildingWikiRestTemplateServiceImpl.class
                .getResourceAsStream("/application-key.properties")) {
            properties.load(inputStream);
        }
    }

    @BeforeEach
    void beforeEach() {
        this.wikiService = new BuildingWikiRestTemplateServiceImpl(properties.getProperty("building-wiki-url"));
    }

    @DisplayName("직접 위키 페이지 확인해 보면서 테스트")
    @Test
    void addPage() {
        wikiService.addPage("Sample-pageasdf");
    }
}