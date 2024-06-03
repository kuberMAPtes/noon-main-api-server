package com.kube.noon.building.dto;
import com.kube.noon.common.zzim.Zzim;

import com.kube.noon.common.zzim.ZzimType;
import lombok.*;

/**
 * Zzim테이블에서 건물 구독에 관한 데이터만 바인딩 할 DTO
 * Logical ERD의 BuildingSubscription에 해당한다.
 *
 *
 * @author 허예지
 */
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BuildingZzimDto {
    private int buildingId;
    private String memberId; //Join 대상이므로 Member 서브시스템 pull 후 타입을 Member로 수정할 예정
    private String subscriptionProviderId; //Join 대상이므로 Member 서브시스템 pull 후 타입을 Member로 수정할 예정
    private ZzimType zzimType;
    private boolean activated;


    //EntityToDto
    public static BuildingZzimDto fromEntity(Zzim zzim) {

        return BuildingZzimDto.builder()
                .memberId(zzim.getMemberId())
                .buildingId(zzim.getBuildingId())
                .subscriptionProviderId(zzim.getSubscriptionProviderId())
                .zzimType(zzim.getZzimType())
                .activated(zzim.isActivated())
                .build();
    }

    //DtoToEntity
    public Zzim toEntity() {
        return Zzim.builder()
                .memberId(this.memberId)
                .feedId(0)
                .buildingId(this.buildingId)
                .subscriptionProviderId(this.subscriptionProviderId)
                .zzimType(this.zzimType)
                .activated(this.activated)
                .build();
    }
}
