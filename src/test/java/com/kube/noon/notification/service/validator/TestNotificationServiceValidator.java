package com.kube.noon.notification.service.validator;

import com.kube.noon.common.validator.IllegalServiceCallException;
import com.kube.noon.member.domain.Member;
import com.kube.noon.member.repository.MemberRepository;
import com.kube.noon.notification.domain.Notification;
import com.kube.noon.notification.domain.NotificationType;
import com.kube.noon.notification.dto.NotificationDto;
import com.kube.noon.notification.repository.NotificationRepository;
import com.kube.noon.notification.service.NotificationServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

@Slf4j
@SpringBootTest
@Transactional
class TestNotificationServiceValidator {
    static final String SAMPLE_MEMBER_ID = "sample-id";

    @Autowired
    NotificationServiceImpl notificationService;

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    NotificationRepository notificationRepository;

    @BeforeEach
    void beforeEach() {
        memberRepository.addMember(generateMemberOfName(SAMPLE_MEMBER_ID));
    }

    @DisplayName("sendNotification() - 없는 회원한테 알림 보내려 하면 실패")
    @Test
    void sendNotification_noSuchMember() {
        NotificationDto dto = NotificationDto.from(new Notification(generateMemberOfName("invalid_member"),
                "Hello", NotificationType.COMMENT));
        checkIfSendNotificationSuccess(dto, "receiverId");
    }

    @DisplayName("sendNotification() - 내용이 빈 알림을 보내려 하면 실패")
    @Test
    void sendNotification_emptyText() {
        NotificationDto dto = NotificationDto.from(
                new Notification(generateMemberOfName(SAMPLE_MEMBER_ID),
                "",
                NotificationType.COMMENT)
        );
        checkIfSendNotificationSuccess(dto, "notificationText");
    }

    @DisplayName("sendNotification() - 없는 유저한테 내용이 빈 알림을 보내려 하면 실패")
    @Test
    void sendNotification_emptyText_and_noSuchMember() {
        NotificationDto dto = NotificationDto.from(
                new Notification(generateMemberOfName("invalid_member"), "", NotificationType.COMMENT)
        );
        checkIfSendNotificationSuccess(dto, "receiverId", "notificationText");
    }

    @DisplayName("sendNotification() - 알림 유형이 null이면 실패")
    @Test
    void sendNotification_notificationTypeIsNull() {
        NotificationDto dto = NotificationDto.from(
                new Notification(generateMemberOfName(SAMPLE_MEMBER_ID), "some text", null)
        );
        checkIfSendNotificationSuccess(dto, "notificationType");
    }

    @DisplayName("sendNotification() - 예외 발생하지 않음")
    @Test
    void sendNotification_noException() {
        NotificationDto dto = NotificationDto.from(
                new Notification(generateMemberOfName(SAMPLE_MEMBER_ID), "some text", NotificationType.COMMENT)
        );
        assertThatNoException().isThrownBy(() -> this.notificationService.sendNotification(dto));
    }

    private void checkIfSendNotificationSuccess(NotificationDto dto, String... causes) {
        try {
            this.notificationService.sendNotification(dto);
            fail("예외가 발생해야 하는데 발생하지 않으면 테스트 실패");
        } catch (IllegalServiceCallException e) {
            assertThat(e.getProblems().keySet())
                    .containsExactlyInAnyOrder(causes);
        } catch (Exception e) {
            fail("IllegalServiceCallException 외에 다른 예외가 던져지면 실패: " + e);
        }
    }

    @DisplayName("getNotificationList(String receiverId) - 없는 회원의 알림 가져오기")
    @Test
    void getNotificationList() {
        try {
            this.notificationService.getNotificationList("invalid_member");;
            fail("예외가 발생해야 하는데 발생하지 않으면 테스트 실패");
        } catch (IllegalServiceCallException e) {
            assertThat(e.getProblems().keySet())
                    .containsExactlyInAnyOrder("receiverId");
        } catch (Exception e) {
            fail("IllegalServiceCallException 외에 다른 예외가 던져지면 실패: " + e);
        }
    }

    @DisplayName("getNotificationList(String receiverId) - 성공 - 빈 리스트 리턴")
    @Test
    void getNotificationList_success_returnEmptyList() {
        try {
            List<NotificationDto> notificationList = this.notificationService.getNotificationList(SAMPLE_MEMBER_ID);
            assertThat(notificationList).isEmpty();
        } catch (Exception e) {
            fail("예외가 발생하면 안 되는데 발생함");
        }
    }

    @DisplayName("getNotificationList(String receiverId) - 성공 - 뭔가 들어 있는 리스트 리턴")
    @Test
    void getNotificationList_success() {
        Member member = this.memberRepository.findMemberById(SAMPLE_MEMBER_ID).get();
        this.notificationRepository.save(new Notification(member, "으아아", NotificationType.COMMENT));

        try {
            List<NotificationDto> notificationList = this.notificationService.getNotificationList(SAMPLE_MEMBER_ID);
            assertThat(notificationList).isNotEmpty();
        } catch (Exception e) {
            fail("예외가 발생하면 안 되는데 발생함");
        }
    }

    private Member generateMemberOfName(String memberName) {
        Member sampleMember = new Member();
        sampleMember.setMemberId(memberName);
        sampleMember.setPwd("1q2w3e4r");
        sampleMember.setPhoneNumber("01012341234");
        sampleMember.setNickname("sample-nickname");
        return sampleMember;
    }
}