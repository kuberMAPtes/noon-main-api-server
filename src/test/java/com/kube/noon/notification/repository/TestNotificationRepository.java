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
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;

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
}
