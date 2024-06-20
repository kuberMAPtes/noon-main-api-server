package com.kube.noon.common;

import com.kube.noon.building.service.BuildingProfileService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
public class ScheduledTasks {

    @Autowired
    @Qualifier("buildingProfileServiceImpl")
    BuildingProfileService buildingProfileService;

    // 피드 요약을 실행할 빌딩 목록
//    @Value("#{'${summary.buildingIds}'.split(',')}")
    private List<Integer> buildingIds;

    //@Scheduled(cron = "0 0 0 * * ?") // 매일 밤 12시에 피드 요약 실행
    @Scheduled(fixedRate = 5000) // 5초마다 실행
    public void performSummary() {

        for (Integer buildingId : buildingIds) {
            String feedAiSummary = buildingProfileService.getFeedAISummary(buildingId);
            log.info("업데이트된"+buildingId+"의 피드 Summary={}",feedAiSummary);
        }

    }

}
