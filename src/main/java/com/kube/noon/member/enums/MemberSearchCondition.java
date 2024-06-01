package com.kube.noon.member.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum MemberSearchCondition {

    MEMBER_ID("memberId"),
    NICKNAME("nickname"),
//    PWD("pwd"),
    UNLOCK_TIME("unlockTime"),
    PHONE_NUMBER("phoneNumber"),
    SIGNED_OFF("signedOff");

    private final String value;


}
