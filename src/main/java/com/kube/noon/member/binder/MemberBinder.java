package com.kube.noon.member.binder;

import com.kube.noon.member.domain.Member;
import lombok.Setter;

@SuppressWarnings("LombokSetterMayBeUsed")
public enum MemberBinder {
    INSTANCE;

    @Setter
    private MemberConverter service;

    public Member toEntity(Object dto) {
        return service.toEntity(dto);
    }

    public Object toDto(Member member, Class<?> dtoClass) {
        return service.toDto(member, dtoClass);
    }
}
