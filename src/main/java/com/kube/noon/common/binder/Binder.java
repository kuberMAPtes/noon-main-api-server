package com.kube.noon.common.binder;

public interface Binder<D, E> {
    E toEntity(D dto);
    D toDto(E entity);
    Class<D> getDtoType();
}
