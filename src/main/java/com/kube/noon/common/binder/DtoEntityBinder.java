package com.kube.noon.common.binder;

import lombok.Setter;

/**
 * 사용법 :: 원하는 Entity = DtoEntityBinder.INSTANCE.toEntity(원하는 Dto);
 */
@SuppressWarnings("LombokSetterMayBeUsed")
public enum DtoEntityBinder {
    INSTANCE;

    @Setter
    private DtoEntityConverter service;

    public <D, E> E toEntity(D dto) {
        return service.toEntity(dto);
    }

    public <D, E> E toResponseDto(D dto) {
        return service.toResponseDto(dto);
    }
    public <D, E> D toDto(E entity, Class<D> dtoClass) {
        return service.toDto(entity, dtoClass);
    }

}
