package com.kube.noon.common.binder;

import lombok.Setter;

@SuppressWarnings("LombokSetterMayBeUsed")
public enum DtoEntityBinder {
    INSTANCE;

    @Setter
    private DtoEntityConverter service;

    public <T, E> E toEntity(T dto) {
        return service.toEntity(dto);
    }

    public <T, E> T toDto(E entity, Class<T> dtoClass) {
        return service.toDto(entity, dtoClass);
    }
}
