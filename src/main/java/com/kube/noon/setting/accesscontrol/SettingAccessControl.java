package com.kube.noon.setting.accesscontrol;

import com.kube.noon.common.security.accesscontrol.AccessControl;
import com.kube.noon.common.security.accesscontrol.AccessControlTrigger;
import com.kube.noon.member.domain.Member;
import com.kube.noon.member.enums.Role;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@AccessControl
public class SettingAccessControl {

    @AccessControlTrigger(path = "/setting/getSetting/{memberId}", method = "GET")
    public boolean preventAccessOfOthers(Member member, HttpServletRequest request) {
        if (member.getMemberRole() == Role.ADMIN) {
            return true;
        }

        String path = request.getRequestURI();
        String pathVariable = path.substring(path.lastIndexOf('/') + 1);
        log.trace("path={}", path);
        log.trace("pathVariable={}", pathVariable);
        log.trace("member.getMemberId()={}", member.getMemberId());
        log.trace("pathVariable.equals(member.getMemberId())={}", pathVariable.equals(member.getMemberId()));
        return pathVariable.equals(member.getMemberId());
    }
}
