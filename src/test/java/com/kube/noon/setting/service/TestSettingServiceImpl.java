package com.kube.noon.setting.service;

import com.kube.noon.common.PublicRange;
import com.kube.noon.member.domain.Member;
import com.kube.noon.member.dto.member.AddMemberDto;
import com.kube.noon.member.service.MemberService;
import com.kube.noon.setting.dto.SettingDto;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
@SpringBootTest
@Transactional
class TestSettingServiceImpl {

    @Autowired
    MemberService memberService;

    @Autowired
    SettingService settingService;

    @BeforeEach
    void beforeEach() {
        this.memberService.addMember(new AddMemberDto("sample-id", "nickname", "1q2w3e4r", "01012341234", false));
    }

    @Test
    void checkDefaultSetting() {
        Member findMember = this.memberService.findMemberById("sample-id").get();
        log.info("findMember={}", findMember);
        assertThat(findMember.getMemberProfilePublicRange()).isEqualTo(PublicRange.PUBLIC);
        assertThat(findMember.getAllFeedPublicRange()).isEqualTo(PublicRange.PUBLIC);
        assertThat(findMember.getBuildingSubscriptionPublicRange()).isEqualTo(PublicRange.PUBLIC);
        assertThat(findMember.getReceivingAllNotificationAllowed()).isTrue();
    }

    @Test
    void updateSetting_plus_findSetting() {
        this.settingService.updateSetting(
                "sample-id",
                SettingDto.builder()
                        .allFeedPublicRange(PublicRange.PUBLIC)
                        .buildingSubscriptionPublicRange(PublicRange.PRIVATE)
                        .memberProfilePublicRange(PublicRange.FOLLOWER_ONLY)
                        .receivingAllNotificationAllowed(true)
                        .build());

        SettingDto findSetting = this.settingService.findSettingOfMember("sample-id");
        log.info("findSetting={}", findSetting);
        assertThat(findSetting.getAllFeedPublicRange()).isEqualTo(PublicRange.PUBLIC);
        assertThat(findSetting.getBuildingSubscriptionPublicRange()).isEqualTo(PublicRange.PRIVATE);
        assertThat(findSetting.getMemberProfilePublicRange()).isEqualTo(PublicRange.FOLLOWER_ONLY);
        assertThat(findSetting.isReceivingAllNotificationAllowed()).isTrue();
    }
}