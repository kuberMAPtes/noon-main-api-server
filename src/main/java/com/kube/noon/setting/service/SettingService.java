package com.kube.noon.setting.service;

import com.kube.noon.setting.dto.SettingDto;

/**
 * 환경설정 서비스 로직 인터페이스
 *
 * @author PGD
 */
public interface SettingService {

    /**
     * 환경설정을 업데이트한다.
     * @param memberId 환경설정이 업데이트될 회원의 ID
     * @param newSetting 적용될 환경설정 정보. 각 환경설정 항목이 null일 경우, 그 환경설정은 반영되지 않는다.
     */
    void updateSetting(String memberId, SettingDto newSetting);

    /**
     * 특정 회원의 환경설정 상태를 얻는다.
     * @param memberId 얻고자 하는 환경설정을 가진 회원의 ID
     * @return 환경설정 정보가 담긴 아이디. 필드의 모든 값은 null이 아니다.
     */
    SettingDto findSettingOfMember(String memberId);
}
