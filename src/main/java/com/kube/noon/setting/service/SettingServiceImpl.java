package com.kube.noon.setting.service;

import com.kube.noon.member.domain.Member;
import com.kube.noon.member.dto.UpdateMemberDto;
import com.kube.noon.member.service.MemberService;
import com.kube.noon.setting.domain.Setting;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;

@Slf4j
@Service
@RequiredArgsConstructor
public class SettingServiceImpl implements SettingService {
    private final MemberService memberService;

    @Override
    public void updateSetting(String memberId, Setting newSetting) {
        UpdateMemberDto dto = UpdateMemberDto.builder()
                .memberId(memberId)
                .allFeedPublicRange(newSetting.getAllFeedPublicRange())
                .memberProfilePublicRange(newSetting.getMemberProfilePublicRange())
                .buildingSubscriptionPublicRange(newSetting.getBuildingSubscriptionPublicRange())
                .receivingAllNotificationAllowed(newSetting.isReceivingAllNotificationAllowed())
                .build();
        log.debug("UpdateMemberDto dto={}", dto);
        this.memberService.updateMember(dto);
    }

    @Override
    public Setting findSettingOfMember(String memberId) {
        Member findMember = this.memberService.findMemberById(memberId).orElseThrow(NoSuchElementException::new);
        return Setting.builder()
                .memberProfilePublicRange(findMember.getMemberProfilePublicRange())
                .allFeedPublicRange(findMember.getAllFeedPublicRange())
                .buildingSubscriptionPublicRange(findMember.getBuildingSubscriptionPublicRange())
                .receivingAllNotificationAllowed(findMember.getReceivingAllNotificationAllowed())
                .build();
    }
}
