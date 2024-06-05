package com.kube.noon.setting.domain;

import com.kube.noon.common.PublicRange;
import lombok.*;

/**
 * 환경설정 도메인 객체
 *
 * @author PGD
 */
@Deprecated
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class Setting {
    private PublicRange memberProfilePublicRange;
    private PublicRange allFeedPublicRange;
    private PublicRange buildingSubscriptionPublicRange;
    private boolean receivingAllNotificationAllowed;
}
