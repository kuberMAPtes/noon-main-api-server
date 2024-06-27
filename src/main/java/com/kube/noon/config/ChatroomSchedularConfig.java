package com.kube.noon.config;

import com.kube.noon.chat.service.DeleteChatroomsJob;
import org.quartz.*;
import org.quartz.impl.JobDetailImpl;
import org.quartz.impl.triggers.SimpleTriggerImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Date;

@Configuration
public class ChatroomSchedularConfig {


    /**
     * Job: Quartz에서 실행할 작업을 정의합니다. Job 인터페이스를 구현한 클래스입니다.
     * 여기서 채팅방 삭제 클래스 'DeleteChatroomJob' 을 연결시키고 있습니다.
     * @return
     */
    @Bean
    public JobDetail deleteChatRoomsJobDetail() {
        JobDetailImpl jobDetail = new JobDetailImpl(); // JobDetailImpl은 JobDetail 인터페이스를 구현하는 여러 클래스 중 하나
        jobDetail.setJobClass(DeleteChatroomsJob.class); // 작업이 실행될 때 수행할 로직을 포함하는 클래스
        jobDetail.setJobDataMap(new JobDataMap()); // 작업 실행 시 필요한 데이터를 전달할 수 있는 맵 (현재는 비어있음)
        jobDetail.setKey(new JobKey("deleteChatRoomsJob")); // 작업을 고유하게 식별하는 키
        jobDetail.setDurability(true);
        // Quartz에서 Job의 내구성을 true로 설정해야 합니다. Job이 트리거 없이도 스케줄러에 의해 유지되도록 설정하는 것입니다.

        /**
         * 만약 Durability 를 true 로 하지 않는다면?
         * Quartz 스케줄러는 트리거가 없는 Job을 내구성이 없다고 간주하여 삭제합니다.
         * 이로 인해 스프링 컨텍스트에서 해당 Job의 Bean을 생성하려 할 때, 스케줄러에서 해당 Job을 찾을 수 없어 오류가 발생합니다.
         */
        return jobDetail;
    }

    /**
     * Trigger: 언제(Job의 실행 시점) 작업이 실행될지를 정의합니다.
     * CronTrigger나 SimpleTrigger 등을 사용할 수 있습니다.
     * @param deleteChatRoomsJobDetail
     * @return
     */
    @Bean
    public SimpleTriggerImpl deleteChatRoomsTrigger(JobDetail deleteChatRoomsJobDetail) {
        SimpleTriggerImpl trigger = new SimpleTriggerImpl();
        trigger.setJobKey(deleteChatRoomsJobDetail.getKey()); // Job키를 받기
        trigger.setRepeatInterval(3600*300); // 5분간격 (밀리초 단위)
        trigger.setRepeatCount(SimpleTrigger.REPEAT_INDEFINITELY); // 무한 반복
        trigger.setKey(new TriggerKey("deleteChatRoomsTrigger"));
        trigger.setStartTime(new Date()); // 트리거 시작 시간 설정
        return trigger;
    }

}
