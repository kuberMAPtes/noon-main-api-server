package com.kube.noon.setting.service;

import com.kube.noon.setting.domain.Setting;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class SettingServiceImpl implements SettingService {

    @Override
    public void updateSetting(String memberId, Setting newSetting) {
        // TODO
        throw new UnsupportedOperationException();
    }

    @Override
    public Setting findSettingOfMember(String memberId) {
        // TODO
        throw new UnsupportedOperationException();
    }
}
