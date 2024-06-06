package com.kube.noon.notification.service;

import com.kube.noon.common.PublicRange;
import com.kube.noon.member.enums.Role;
import com.kube.noon.member.repository.MemberRepository;
import com.kube.noon.notification.domain.NotificationType;
import com.kube.noon.notification.dto.NotificationDto;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

@Slf4j
@Transactional
@SpringBootTest
class TestNotificationServiceImpl {

    @Autowired
    NotificationServiceImpl notificationService;

    @Autowired
    MemberRepository memberRepository;

    @DisplayName("알림 전송 - 성공")
    @Test
    void sendNotification_success() {
        getAndAddSampleReceiver();

        NotificationDto notificationDto = new NotificationDto();
        notificationDto.setReceiverId("sample-receiver");
        notificationDto.setNotificationType(NotificationType.COMMENT);
        notificationDto.setNotificationText("sample-text");
        assertThatNoException().isThrownBy(() -> this.notificationService.sendNotification(notificationDto));
    }

    @DisplayName("알림 전송 - 수신자가 DB에 없음")
    @Test
    void sendNotification_receiverNotExists() {
        getAndAddSampleReceiver();

        NotificationDto notificationDto = new NotificationDto();
        notificationDto.setReceiverId("no-exist");
        notificationDto.setNotificationType(NotificationType.COMMENT);
        notificationDto.setNotificationText("sample-text");
        assertThatExceptionOfType(Exception.class)
                .isThrownBy(() -> this.notificationService.sendNotification(notificationDto));
    }

    @DisplayName("알림 조회")
    @Test
    void getNotification() {
        getAndAddSampleReceiver();

        NotificationDto notificationDto = new NotificationDto();
        notificationDto.setReceiverId("sample-receiver");
        notificationDto.setNotificationType(NotificationType.COMMENT);
        notificationDto.setNotificationText("sample-text");

        System.out.println(notificationDto);

        this.notificationService.sendNotification(notificationDto);

        List<NotificationDto> notificationList = this.notificationService.getNotificationList("sample-receiver");

        log.info("notificationList={}", notificationList);

        assertThat(notificationList.size()).isEqualTo(1);

        NotificationDto notification = notificationList.get(0);

        assertThat(notification.getReceiverId()).isEqualTo(notificationDto.getReceiverId());
        assertThat(notification.getNotificationText()).isEqualTo(notificationDto.getNotificationText());
        assertThat(notification.getNotificationType()).isEqualTo(notificationDto.getNotificationType());
    }

    @Test
    void getNotification_zeroSize() {
        getAndAddSampleReceiver();

        NotificationDto notificationDto = new NotificationDto();
        notificationDto.setReceiverId("sample-receiver");
        notificationDto.setNotificationType(NotificationType.COMMENT);
        notificationDto.setNotificationText("sample-text");

        System.out.println(notificationDto);

        this.notificationService.sendNotification(notificationDto);

        List<NotificationDto> notificationList = this.notificationService.getNotificationList("not-exists");

        log.info("notificationList={}", notificationList);

        assertThat(notificationList).isEmpty();
    }

    private com.kube.noon.member.domain.Member getAndAddSampleReceiver() {
        com.kube.noon.member.domain.Member newMember = new com.kube.noon.member.domain.Member(
                "sample-receiver",
                Role.MEMBER,
                "sample-nickname",
                "sample-pwd",
                "01012341234",
                LocalDateTime.now(),
                null,
                null,
                0,
                false,
                PublicRange.PUBLIC,
                PublicRange.PUBLIC,
                PublicRange.PUBLIC,
                true
        );
        this.memberRepository.addMember(newMember);
        return newMember;
    }
}