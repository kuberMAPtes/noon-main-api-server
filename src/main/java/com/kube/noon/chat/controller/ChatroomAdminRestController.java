package com.kube.noon.chat.controller;

import com.kube.noon.chat.dto.ChatroomDto;
import com.kube.noon.chat.service.ChatroomSearchService;
import com.kube.noon.chat.service.ChatroomService;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.TriggerKey;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/adminChatroom")
public class ChatroomAdminRestController {

    private final ChatroomSearchService chatroomSearchService;
    private final ChatroomService chatroomService;

    @Autowired
    public ChatroomAdminRestController(ChatroomSearchService chatroomSearchService, ChatroomService chatroomService) {
        this.chatroomSearchService = chatroomSearchService;
        this.chatroomService = chatroomService;
    }

    /**
     * 채팅방 검색
     * @param searchKeywordChatroomName
     * @return
     * @throws Exception
     */
    @GetMapping("chatroomSearch")
    public List<ChatroomDto> chatroomSearch(String searchKeywordChatroomName) throws Exception {
        List<ChatroomDto> chatrooms = chatroomSearchService.getChatroomListByChatroomName(searchKeywordChatroomName);

        return chatrooms;
    }

    /**
     * 채팅방 삭제
     * @param chatroomId
     * @return
     * @throws Exception
     */
    @GetMapping("chatroomDelete")
    public String chatroomDelete(int chatroomId) throws Exception {

        return chatroomService.deleteChatroom(chatroomId);
    }

    /**
     * 의존성 Quartz 의 schedular 함수를 통 채팅방 자동삭제 시간을 반환
     * @return
     */
    @Autowired
    private Scheduler scheduler;

    @GetMapping("/chatroomDeleteTime")
    public long getNextTriggerTime() {
        System.out.println("        🐬[Admin Controller] 채팅방 폭파시간 알아오기 실행)");
        try {
            /**
             * TriggerKey를 통해 Quartz Scheduler에 등록된 특정 트리거를 식별하고 가져올 수 있다.
             * TriggerKey는 트리거의 이름과 그룹을 가지고 식별한다. (TriggerKey의 두번째 인자가 그룹임. 현재는 그룹은 없음 DEFAULT로 세팅됨)
             */
            Trigger trigger = scheduler.getTrigger(new TriggerKey("deleteChatRoomsTrigger"));
            if (trigger != null) {
                Date nextFireTime = trigger.getNextFireTime(); // 다음 트리거 실행시간
                System.out.println("nextFireTime: " + nextFireTime.getTime());
                System.out.println("System.currentTimeMillis(): " + System.currentTimeMillis());
                System.out.println("남은시간" + (nextFireTime.getTime() - System.currentTimeMillis()));
                return nextFireTime.getTime() - System.currentTimeMillis(); // 남은 시간 (= 다음 트리거 실행시간 - 현재시간)
            }
        } catch (SchedulerException e) {
            e.printStackTrace();
        }
        return -1;
    }
}
