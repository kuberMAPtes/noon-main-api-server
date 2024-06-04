package com.kube.noon.config;

import com.kube.noon.building.service.BuildingWikiService;
import com.kube.noon.building.service.buildingwiki.BuildingWikiEmptyServiceImpl;
import com.kube.noon.common.logging.TraceLoggingAspect;
import com.kube.noon.common.validator.ValidationAspect;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

/**
 * Spring Bean 수동 등록을 위한 Configuration 클래스
 *
 * @author PGD
 */
@Configuration
@EnableAspectJAutoProxy
public class BeanConfig {

    @Bean
    @ConditionalOnMissingBean(BuildingWikiService.class) // Spring Boot @Conditional에 대해서 알아 보면 좋음
    public BuildingWikiEmptyServiceImpl buildingWikiEmptyService() {
        return new BuildingWikiEmptyServiceImpl();
    }

    @Bean
    public TraceLoggingAspect traceLoggingAspect() {
        return new TraceLoggingAspect();
    }

    @Bean
    public ValidationAspect validationAspect(ApplicationContext applicationContext) {
        return new ValidationAspect(applicationContext);
    }
}
