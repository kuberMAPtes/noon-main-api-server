package com.kube.noon.config;

import com.kube.noon.building.service.buildingwiki.BuildingWikiEmptyServiceImpl;
import com.kube.noon.building.service.BuildingWikiService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Spring Bean 수동 등록을 위한 Configuration 클래스
 *
 * @author PGD
 */
@Configuration
public class BeanConfig {

    @Bean
    @ConditionalOnMissingBean(BuildingWikiService.class) // Spring Boot @Conditional에 대해서 알아 보면 좋음
    public BuildingWikiEmptyServiceImpl buildingWikiEmptyService() {
        return new BuildingWikiEmptyServiceImpl();
    }
}
