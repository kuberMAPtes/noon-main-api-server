package com.kube.noon.config;

import com.kube.noon.building.service.BuildingWikiEmptyServiceImpl;
import com.kube.noon.building.service.BuildingWikiService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BeanConfig {

    @Bean
    @ConditionalOnMissingBean(BuildingWikiService.class)
    public BuildingWikiEmptyServiceImpl buildingWikiEmptyService() {
        return new BuildingWikiEmptyServiceImpl();
    }
}
