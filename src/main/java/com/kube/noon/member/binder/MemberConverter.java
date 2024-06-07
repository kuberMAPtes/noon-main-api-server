package com.kube.noon.member.binder;

import com.kube.noon.member.domain.Member;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * DTO와 Member 간의 변환을 위한 컨버터 클래스
 */
@Component
public class MemberConverter {

    /**
     * Binder를 저장하는 맵입니다.
     */
    private final Map<Class<?>, Binder<?>> converters = new HashMap<>();

    /**
     * 스프링에서 주입받은 Binder 리스트입니다.
     */
    private final List<Binder<?>> converterList;

    /**
     * MemberConverter의 생성자입니다.
     *
     * @param converterList 스프링에서 주입받은 Binder 리스트입니다.
     */
    @Autowired
    public MemberConverter(List<Binder<?>> converterList) {
        this.converterList = converterList;
    }

    /**
     * 주입받은 Binder 리스트를 사용하여 맵을 초기화합니다.
     */
    @PostConstruct
    public void initialize() {
        System.out.println("실행되는지 확인");
        for (Binder<?> converter : converterList) {
            converters.put(converter.getDtoType(), converter);
        }
        MemberBinder.INSTANCE.setService(this);
    }

    @SuppressWarnings("unchecked")
    public <T> Member toEntity(T dto) {
        Binder<T> converter = (Binder<T>) converters.get(dto.getClass());
        if (converter == null) {
            throw new IllegalArgumentException("지원되지 않는 DTO 타입입니다: " + dto.getClass());
        }
        return converter.toEntity(dto);
    }

    @SuppressWarnings("unchecked")
    public <T> T toDto(Member member, Class<T> dtoClass) {
        Binder<T> converter = (Binder<T>) converters.get(dtoClass);
        if (converter == null) {
            throw new IllegalArgumentException("지원되지 않는 DTO 타입입니다: " + dtoClass);
        }
        return converter.toDto(member);
    }
}
