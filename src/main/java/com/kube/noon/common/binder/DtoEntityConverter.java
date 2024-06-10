package com.kube.noon.common.binder;

import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ResolvableType;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * DTO와 Entity 간의 변환을 위한 컨버터 클래스
 */
@Component
public class DtoEntityConverter {

    private static final Logger logger = LoggerFactory.getLogger(DtoEntityConverter.class);

    private final Map<Class<?>, Binder<?, ?>> converters = new HashMap<>();
    private final List<Binder<?, ?>> converterList;

    @Autowired
    public DtoEntityConverter(List<Binder<?, ?>> converterList) {
//       Binder 인터페이스를 구현한 모든 빈을 찾아서 주입
        this.converterList = converterList;
    }

    @PostConstruct
    public void initialize() {
        logger.info("초기화 시작");
        logger.info("컨버터 리스트 크기: {}", converterList.size());

        for (Binder<?, ?> converter : converterList) {
            logger.info("처리 중인 컨버터: {}", converter.getClass().getName());

            // ResolvableType을 사용하여 제네릭 타입 파라미터 추출
            ResolvableType resolvableType = ResolvableType.forClass(converter.getClass()).as(Binder.class);
            Class<?> dtoType = resolvableType.getGeneric(0).resolve();
            if (dtoType != null) {
                converters.put(dtoType, converter);
                logger.info("DTO 타입: {}", dtoType.getName());
            } else {
                logger.warn("DTO 타입을 추출할 수 없습니다: {}", converter.getClass().getName());
            }
        }

        logger.info("컨버터 수: {}", converters.size());
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
    public <D, E> E toResponseDto(D dto) {
        Binder<D, E> converter = (Binder<D, E>) converters.get(dto.getClass());
        if (converter == null) {
            throw new IllegalArgumentException("지원되지 않는 DTO 타입입니다: " + dto.getClass());
        }
        return converter.toResponseDto(dto);
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
