package com.kube.noon.notification.converter;

import com.kube.noon.notification.domain.NotificationType;
import jakarta.persistence.AttributeConverter;

/**
 * NotificationType을 데이터베이스에 저장하기 위한 데이터 컨버터
 * NotificationType enum을 String 객체로 변환
 * @author PGD
 */
public class NotificationTypeConverter
        implements AttributeConverter<NotificationType, String> {

    @Override
    public String convertToDatabaseColumn(NotificationType attribute) {
        return attribute.name();
    }

    @Override
    public NotificationType convertToEntityAttribute(String dbData) {
        return NotificationType.valueOf(dbData);
    }
}
