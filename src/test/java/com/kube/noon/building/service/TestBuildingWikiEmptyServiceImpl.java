package com.kube.noon.building.service;

import com.kube.noon.building.service.buildingwiki.BuildingWikiEmptyServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class TestBuildingWikiEmptyServiceImpl {

    @Autowired
    BuildingWikiService buildingWikiService;

    @Autowired
    Environment env;

    @Test
    void beanTest() {
        if (Arrays.asList(env.getActiveProfiles()).contains("prod")) {
            assertThat(this.buildingWikiService).isNotInstanceOf(BuildingWikiEmptyServiceImpl.class);
        } else {
            assertThat(this.buildingWikiService).isInstanceOf(BuildingWikiEmptyServiceImpl.class);
        }
    }
}