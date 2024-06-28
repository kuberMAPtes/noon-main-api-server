package com.kube.noon.member.dto.member;

import com.kube.noon.member.dto.member.MemberProfileDto;

public class ProfileAccessResultDto {
    private boolean canAccess;
    private String message;
    private MemberProfileDto memberProfile;

    public ProfileAccessResultDto(boolean canAccess, String message, MemberProfileDto memberProfile) {
        this.canAccess = canAccess;
        this.message = message;
        this.memberProfile = memberProfile;
    }

    // Getters and setters
    public boolean isCanAccess() {
        return canAccess;
    }

    public void setCanAccess(boolean canAccess) {
        this.canAccess = canAccess;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public MemberProfileDto getMemberProfile() {
        return memberProfile;
    }

    public void setMemberProfile(MemberProfileDto memberProfile) {
        this.memberProfile = memberProfile;
    }
}
