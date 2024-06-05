package com.kube.noon.config;

import com.kube.noon.building.service.buildingwiki.BuildingWikiEmptyServiceImpl;
import com.kube.noon.building.service.BuildingWikiService;
import com.kube.noon.notification.service.sender.CoolSmsNotificationAgent;
import com.kube.noon.notification.service.sender.NotificationEmptyAgent;
import com.kube.noon.notification.service.sender.NotificationTransmissionAgent;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

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

    @Value("${cool-sms.access-key}") String coolSmsAccessKey;
    @Value("${cool-sms.secret-key}") String coolSmsSecretKey;
    @Value("${cool-sms.from-phone-number}") String fromPhoneNumber;

    @Bean
    @Profile("prod")
    public NotificationTransmissionAgent transmissionAgentForProd() {
        return new CoolSmsNotificationAgent(coolSmsAccessKey, coolSmsSecretKey, fromPhoneNumber);
    }

    @Bean
    @ConditionalOnMissingBean(NotificationTransmissionAgent.class)
    public NotificationTransmissionAgent transmissionAgentForDev() {
        return new NotificationEmptyAgent();
    }
}
