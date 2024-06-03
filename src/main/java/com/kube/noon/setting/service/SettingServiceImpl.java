package com.kube.noon.setting.service;

import com.kube.noon.member.binder.MemberBinder;
import com.kube.noon.member.domain.Member;
import com.kube.noon.member.dto.UpdateMemberDto;
import com.kube.noon.member.service.MemberService;
import com.kube.noon.setting.domain.Setting;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class SettingServiceImpl implements SettingService {
    private final MemberService memberService;

    @Override
    public void updateSetting(String memberId, Setting newSetting) {
        Member findMember = this.memberService.findMemberById(memberId);
        UpdateMemberDto dto = MemberBinder.INSTANCE.toDto(findMember, UpdateMemberDto.class);
        dto.setMemberProfilePublicRange(newSetting.getMemberProfilePublicRange());
        dto.setAllFeedPublicRange(newSetting.getAllFeedPublicRange());
        dto.setBuildingSubscriptionPublicRange(newSetting.getBuildingSubscriptionPublicRange());
        dto.setReceivingAllNotificationAllowed(newSetting.isReceivingAllNotificationAllowed());
        this.memberService.updateMember(dto);
    }

    @Override
    public Setting findSettingOfMember(String memberId) {
        Member findMember = this.memberService.findMemberById(memberId);
        return Setting.builder()
                .memberProfilePublicRange(findMember.getMemberProfilePublicRange())
                .allFeedPublicRange(findMember.getAllFeedPublicRange())
                .buildingSubscriptionPublicRange(findMember.getBuildingSubscriptionPublicRange())
                .receivingAllNotificationAllowed(findMember.isReceivingAllNotificationAllowed())
                .build();
    }
}
