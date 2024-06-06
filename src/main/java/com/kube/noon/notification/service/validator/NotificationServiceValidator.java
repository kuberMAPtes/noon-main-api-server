package com.kube.noon.notification.service.validator;

import com.kube.noon.common.validator.IllegalServiceCallException;
import com.kube.noon.common.validator.Problems;
import com.kube.noon.common.validator.Validator;
import com.kube.noon.member.domain.Member;
import com.kube.noon.member.exception.MemberNotFoundException;
import com.kube.noon.member.service.MemberService;
import com.kube.noon.notification.dto.NotificationDto;
import com.kube.noon.notification.repository.NotificationRepository;
import com.kube.noon.notification.service.NotificationServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Map;
import java.util.Optional;

@Validator(targetClass = NotificationServiceImpl.class)
@RequiredArgsConstructor
@Transactional
public class NotificationServiceValidator {
    private final NotificationRepository notificationRepository;
    private final MemberService memberService;

    public Problems sendNotification(NotificationDto notification) {
        Problems problems = new Problems();
        checkIfTheMemberExists(notification.getReceiverId(), problems);
        if (doesNotHaveText(notification.getNotificationText())) {
            problems.put("notificationText", "알림 텍스트가 null이거나 nullstring입니다.");
        }
        if (notification.getNotificationType() == null) {
            problems.put("notificationType", "알림 유형이 null입니다.");
        }
        return problems;
    }

    private boolean doesNotHaveText(String text) {
        return !StringUtils.hasText(text);
    }

    public boolean getNotification(int notificationId) {
        this.notificationRepository.findById(notificationId)
                .orElseThrow(() ->
                        new IllegalServiceCallException(
                                new Problems(Map.of("notificationId",
                                        "해당 id에 해당하는 notification이 없습니다: " + notificationId)
                                )
                        )
                );
        return true;
    }

    public Problems getNotificationList(String receiverId) {
        Problems problems = new Problems();
        checkIfTheMemberExists(receiverId, problems);
        return problems;
    }

    private void checkIfTheMemberExists(String receiverId, Map<String, Object> problems) {
        try {
            Optional<Member> member = this.memberService.findMemberById(receiverId);
            member.ifPresentOrElse(System.out::println, () -> System.out.println("없는 멤버"));
        } catch (MemberNotFoundException e) {
            problems.put("receiverId", "No such member of id=" + receiverId);
        }
    }
}
