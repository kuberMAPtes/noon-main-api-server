package com.kube.noon.member.binder;

import com.kube.noon.member.domain.Member;
import lombok.Setter;

@SuppressWarnings("LombokSetterMayBeUsed")
public enum MemberBinder {
    INSTANCE;

    @Setter
    private MemberConverter service;

    public <T> Member toEntity(T dto) {
        return service.toEntity(dto);
    }

    public <T> T toDto(Member member, Class<T> dtoClass) {
        return service.toDto(member, dtoClass);
    }
}
