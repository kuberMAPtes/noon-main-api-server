package com.kube.noon.building.dto;

import com.kube.noon.member.dto.member.MemberDto;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SubscriberDto {

    private MemberDto member;
    private boolean isVisible;

}
