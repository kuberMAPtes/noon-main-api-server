package com.kube.noon.setting.dto;

import com.kube.noon.common.PublicRange;
import lombok.*;

/**
 * 환경설정 DTO
 *
 * @author PGD
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class SettingDto {
    private PublicRange memberProfilePublicRange;
    private PublicRange allFeedPublicRange;
    private PublicRange buildingSubscriptionPublicRange;
    private boolean receivingAllNotificationAllowed;
}
