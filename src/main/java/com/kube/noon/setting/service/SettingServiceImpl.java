package com.kube.noon.setting.service;

import com.kube.noon.member.domain.Member;
import com.kube.noon.member.dto.member.UpdateMemberDto;
import com.kube.noon.member.service.MemberService;
import com.kube.noon.setting.dto.SettingDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;

@Slf4j
@Service
@Primary
@RequiredArgsConstructor
public class SettingServiceImpl implements SettingService {
    private final MemberService memberService;

    @Override
    public void updateSetting(String memberId, SettingDto newSetting) {
        UpdateMemberDto dto = new UpdateMemberDto();
        dto.setMemberId(memberId);
        BeanUtils.copyProperties(newSetting, dto);
        log.debug("UpdateMemberDto dto={}", dto);
        this.memberService.updateMember(dto);
    }

    @Override
    public SettingDto findSettingOfMember(String memberId) {
        Member findMember = this.memberService.findMemberById(memberId).orElseThrow(NoSuchElementException::new);
        SettingDto dto = new SettingDto();
        BeanUtils.copyProperties(findMember, dto);
        return dto;
    }
}
