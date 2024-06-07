package com.kube.noon.member.binder;

import com.kube.noon.member.domain.Member;

public interface Binder<T> {
    Member toEntity(T dto);
    T toDto(Member member);
    Class<T> getDtoType();
}
