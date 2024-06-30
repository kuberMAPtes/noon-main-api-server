package com.kube.noon.chat.service;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * ChatroomSchedularConfig 에서 설정한 DeleteChatroomJob 을 구현함
 */
@Component
public class DeleteChatroomsJob implements Job {

    @Autowired
    private ChatroomService chatroomService;

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {

        System.out.println("Deleting expired chat rooms..."); // 콘솔에 출력 (실제 삭제 로직을 여기에 구현)

        int AutoDeletedChatroomsSize = 0;
        try {
            AutoDeletedChatroomsSize = chatroomService.scheduledDeleteGroupChatrooms();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        if (AutoDeletedChatroomsSize > 0) {
            System.out.println("Deleted " + AutoDeletedChatroomsSize + " expired chat rooms.");
        }
    }
}