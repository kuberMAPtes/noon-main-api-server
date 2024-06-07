package com.kube.noon.setting.service.validator;

import com.kube.noon.common.PublicRange;
import com.kube.noon.common.validator.IllegalServiceCallException;
import com.kube.noon.member.domain.Member;
import com.kube.noon.member.repository.MemberRepository;
import com.kube.noon.setting.dto.SettingDto;
import com.kube.noon.setting.service.SettingService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.*;

@Slf4j
@SpringBootTest
@Transactional
@ActiveProfiles({ "key", "privpark" })
class TestSettingServiceValidator {

    @Autowired
    private SettingService settingService;

    @Autowired
    private MemberRepository memberRepository;

    @DisplayName("updateSetting() - 존재하지 않는 회원의 설정을 변경하려 할 경우")
    @Test
    void updateSetting_noSuchMember() {
        SettingDto dto = SettingDto.builder()
                .buildingSubscriptionPublicRange(PublicRange.PUBLIC)
                .allFeedPublicRange(PublicRange.PUBLIC)
                .memberProfilePublicRange(PublicRange.PUBLIC)
                .receivingAllNotificationAllowed(true)
                .build();
        assertThatExceptionOfType(Exception.class)
                .isThrownBy(() -> this.settingService.updateSetting("member-not-exists", dto));
    }

    @DisplayName("updateSetting() - SettingDto 인스턴스에 null 존재")
    @Test
    void updateSetting_nullExists_inSettingDtoInstance() {
        String sampleMemberId = insertSampleMember();

        // allFeedPublicRange와 memberProfilePublicRange는 null
        SettingDto dto = SettingDto.builder()
                .buildingSubscriptionPublicRange(PublicRange.PUBLIC)
                .receivingAllNotificationAllowed(true)
                .build();

        try {
            this.settingService.updateSetting(sampleMemberId, dto);
            fail("An exception is expected to be thrown, but no exception hasn't been thrown");
        } catch (IllegalServiceCallException e) {
            Map<String, Object> problems = e.getProblems();
            log.info("problems={}", problems);
            Set<String> causes = problems.keySet();
            assertThat(causes)
                    .containsExactlyInAnyOrder("allFeedPublicRange", "memberProfilePublicRange");
        } catch (Exception e) {
            fail("IllegalServiceCallException hasn't been thrown, but some exception has been thrown");
        }
    }

    @DisplayName("updateSetting() - 성공")
    @Test
    void updateSetting_success() {
        String memberId = insertSampleMember();

        SettingDto dto = SettingDto.builder()
                .buildingSubscriptionPublicRange(PublicRange.PUBLIC)
                .allFeedPublicRange(PublicRange.PRIVATE)
                .memberProfilePublicRange(PublicRange.FOLLOWER_ONLY)
                .receivingAllNotificationAllowed(true)
                .build();

        assertThatNoException().isThrownBy(() -> this.settingService.updateSetting(memberId, dto));
    }

    @DisplayName("findSettingOfMember() - 존재하지 않는 회원의 환경설정 조회 시도")
    @ValueSource(booleans = { true, false })
    @ParameterizedTest
    void findSettingOfMember_noSuchMember(boolean insertSampleMember) {
        // MemberServiceImpl에 있는 버그 고치면 테스트 성공
        if (insertSampleMember) {
            insertSampleMember();
        }

        assertThatExceptionOfType(IllegalServiceCallException.class)
                .isThrownBy(() -> this.settingService.findSettingOfMember("member_not_exists"));
    }

    @DisplayName("findSettingOfMember() - 성공")
    @Test
    void findSettingOfMember_success() {
        // MemberServiceImpl에 있는 버그 고치면 테스트 성공
        String sampleMemberId = insertSampleMember();

        assertThatNoException()
                .isThrownBy(() -> this.settingService.findSettingOfMember(sampleMemberId));
    }

    private String insertSampleMember() {
        final String sampleMemberId = "sample-member-id";
        Member sampleMember = new Member();
        sampleMember.setMemberId(sampleMemberId);
        sampleMember.setPwd("1q2w3e4r");
        sampleMember.setPhoneNumber("01012341234");
        sampleMember.setNickname("sample-nickname");
        memberRepository.addMember(sampleMember);
        return sampleMemberId;
    }
}