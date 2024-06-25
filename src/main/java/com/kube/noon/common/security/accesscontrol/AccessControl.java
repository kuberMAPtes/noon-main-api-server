package com.kube.noon.common.security.accesscontrol;

import com.kube.noon.member.domain.Member;
import jakarta.servlet.http.HttpServletRequest;

public interface AccessControl {

    boolean isAllowed(Member member, HttpServletRequest request);
}
