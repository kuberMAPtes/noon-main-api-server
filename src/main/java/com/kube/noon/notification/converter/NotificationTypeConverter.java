package com.kube.noon.notification.converter;

import com.kube.noon.notification.domain.NotificationType;
import jakarta.persistence.AttributeConverter;

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
