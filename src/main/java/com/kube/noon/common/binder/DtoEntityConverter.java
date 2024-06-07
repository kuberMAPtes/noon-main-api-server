package com.kube.noon.common.binder;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class DtoEntityConverter {

    private final Map<Class<?>, Binder<?, ?>> converters = new HashMap<>();
    private final List<Binder<?, ?>> converterList;

    @Autowired
    public DtoEntityConverter(List<Binder<?, ?>> converterList) {
        this.converterList = converterList;
    }

    @PostConstruct
    public void initialize() {
        System.out.println("실행되는지 확인");
        for (Binder<?, ?> converter : converterList) {
            converters.put(converter.getDtoType(), converter);// 엔티티 타입도 필요하면 저장
        }
        DtoEntityBinder.INSTANCE.setService(this);
    }

    @SuppressWarnings("unchecked")
    public <D, E> E toEntity(D dto) {
        Binder<D, E> converter = (Binder<D, E>) converters.get(dto.getClass());
        if (converter == null) {
            throw new IllegalArgumentException("지원되지 않는 DTO 타입입니다: " + dto.getClass());
        }
        return converter.toEntity(dto);
    }

    @SuppressWarnings("unchecked")
    public <D, E> D toDto(E entity, Class<D> dtoClass) {
        Binder<D, E> converter = (Binder<D, E>) converters.get(dtoClass);
        if (converter == null) {
            throw new IllegalArgumentException("지원되지 않는 엔티티 타입입니다: " + entity.getClass());
        }
        return converter.toDto(entity);
    }
}
