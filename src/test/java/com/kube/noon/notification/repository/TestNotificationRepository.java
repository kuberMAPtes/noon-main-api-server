package com.kube.noon.notification.repository;

import com.kube.noon.notification.domain.Notification;
import com.kube.noon.notification.domain.NotificationType;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.jdbc.support.JdbcUtils;
import org.springframework.test.annotation.Commit;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

/**
 * @author PGD
 */
@Slf4j
@SpringBootTest
@Transactional
public class TestNotificationRepository {

    @Autowired
    DataSource dataSource;

    @Autowired
    NotificationRepository notificationRepository;

    @BeforeEach
    void init() {
        // 참조 무결성을 위해 members 테이블에 데이터 삽입
        String sql = """
                INSERT INTO members (
                    member_id,
                    nickname,
                    pwd,
                    phone_number,
                    building_subscription_public_range,
                    all_feed_public_range,
                    member_profile_public_range,
                    receiving_all_notification_allowed
                ) VALUES (
                    'sample-receiver',
                    'sample-nickname',
                    'sample-pwd',
                    '01012341234',
                    'PUBLIC',
                    'PUBLIC',
                    'PUBLIC',
                    TRUE
                )
                """;

        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = DataSourceUtils.getConnection(this.dataSource);
            stmt = conn.prepareStatement(sql);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            if (stmt != null) {
                JdbcUtils.closeStatement(stmt);
            }

            if (conn != null) {
                // 트랜잭션이 끝나기 전까지 Connection을 클로즈하지 않기 위해
                DataSourceUtils.releaseConnection(conn, this.dataSource);
            }
        }
    }

    @DisplayName("Notification 저장")
    @Test
    void save() {
        Notification testCase =
                new Notification("sample-receiver", "sample-text", NotificationType.COMMENT);
        assertThatNoException().isThrownBy(() -> this.notificationRepository.save(testCase));

        Integer notificationId = testCase.getNotificationId();

        assertThat(notificationId).isNotNull();
    }

    @DisplayName("Notification 저장 - notificationId 값 세팅")
    @Test
    void save_setNotificationId() {
        Notification testCase = new Notification();
        testCase.setNotificationId(1);
        testCase.setReceiverId("sample-receiver");
        testCase.setNotificationText("sample-text");
        testCase.setNotificationType(NotificationType.COMMENT);
        this.notificationRepository.save(testCase);

        log.info("testCase.getNotificationId={}", testCase.getNotificationId());

        assertThat(testCase.getNotificationId()).isEqualTo(1);

        Optional<Notification> byId = this.notificationRepository.findById(testCase.getNotificationId());

        // testCase.setNotificationId(1) 메소드로 notificationId를 세팅해도
        // GenerationType.IDENTITY인 이상 notification_id는 AUTO_INCREMENT에 의해
        // 자동으로 세팅돼 저장된다.
        // 즉, notification_id가 1인 레코드는 데이터베이스에 없다.
        assertThat(byId.isEmpty()).isTrue();
        assertThatExceptionOfType(NoSuchElementException.class)
                .isThrownBy(() -> byId.get());
    }

    @DisplayName("Notification 조회")
    @Test
    void findById() {
        Notification testCase =
                new Notification("sample-receiver", "sample-text", NotificationType.COMMENT);

        this.notificationRepository.save(testCase);

        Integer notificationId = testCase.getNotificationId();

        Optional<Notification> findNotificationOp = this.notificationRepository.findById(notificationId);

        assertThat(findNotificationOp.isPresent()).isTrue();

        Notification findNotification = findNotificationOp.get();

        log.info("findNotification={}", findNotification);

        assertThat(findNotification.getNotificationId()).isEqualTo(testCase.getNotificationId());
        assertThat(findNotification.getReceiverId()).isEqualTo(testCase.getReceiverId());
        assertThat(findNotification.getNotificationText()).isEqualTo(testCase.getNotificationText());
        assertThat(findNotification.getNotificationType()).isEqualTo(testCase.getNotificationType());
    }

    @DisplayName("Notification 조회 - 없는 레코드 조회")
    @Test
    void findById_notExistRecord() {
        Notification testCase =
                new Notification("sample-receiver", "sample-text", NotificationType.COMMENT);

        this.notificationRepository.save(testCase);

        Optional<Notification> findNotification = this.notificationRepository.findById(Integer.MAX_VALUE);

        assertThat(findNotification.isEmpty()).isTrue();
        assertThatExceptionOfType(NoSuchElementException.class)
                .isThrownBy(findNotification::get);
    }
}
