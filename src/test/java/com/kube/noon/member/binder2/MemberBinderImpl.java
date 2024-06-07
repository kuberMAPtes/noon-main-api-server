package com.kube.noon.member.binder2;

import org.springframework.stereotype.Service;

/**
 * DTO와 Member 엔티티 간의 변환을 위한 서비스입니다.
 */
@Service
public class MemberBinderImpl implements MemberBinder {

//    /**
//     * 각 DTO 타입에 대한 변환기를 저장하는 맵입니다.
//     */
//    private final Map<Class<?>, MemberConverter<?>> converters = new HashMap<>();
//
//    /**
//     * 스프링에서 주입받은 변환기 리스트입니다.
//     */
//    private final List<MemberConverter<?>> converterList;
//
//    /**
//     * MemberConversionService의 생성자입니다.
//     *
//     * @param converterList 스프링에서 주입받은 변환기 리스트입니다.
//     */
//    @Autowired
//    public MemberBinderImpl(List<MemberConverter<?>> converterList) {
//        this.converterList = converterList;
//    }
//
//    /**
//     * 주입받은 변환기 리스트를 사용하여 converters 맵을 초기화합니다.
//     * 이 메서드는 빈이 생성된 후 비동기로 실행됩니다.
//     */
//    @PostConstruct
//    @Async
//    public void initializeConverters() {
//        for (MemberConverter<?> converter : converterList) {
//            converters.put(converter.getDtoType(), converter);
//        }
//    }
//
//    /**
//     * DTO를 Member 엔티티로 변환합니다.
//     *
//     * @param dto 변환할 DTO입니다.
//     * @param <T> DTO의 타입입니다.
//     * @return 변환된 Member 엔티티입니다.
//     * @throws IllegalArgumentException 지원되지 않는 DTO 타입인 경우 발생합니다.
//     */
//    @SuppressWarnings("unchecked")
//    public <T> Member toEntity(T dto) {
//        MemberConverter<T> converter = (MemberConverter<T>) converters.get(dto.getClass());
//        if (converter == null) {
//            throw new IllegalArgumentException("지원되지 않는 DTO 타입입니다: " + dto.getClass());
//        }
//        return converter.toEntity(dto);
//    }
//
//    /**
//     * Member 엔티티를 DTO로 변환합니다.
//     *
//     * @param member  변환할 Member 엔티티입니다.
//     * @param dtoClass 변환할 DTO의 클래스입니다.
//     * @param <T>     DTO의 타입입니다.
//     * @return 변환된 DTO입니다.
//     * @throws IllegalArgumentException 지원되지 않는 DTO 타입인 경우 발생합니다.
//     */
//    @SuppressWarnings("unchecked")
//    public <T> T toDto(Member member, Class<T> dtoClass) {
//        MemberConverter<T> converter = (MemberConverter<T>) converters.get(dtoClass);
//        if (converter == null) {
//            throw new IllegalArgumentException("지원되지 않는 DTO 타입입니다: " + dtoClass);
//        }
//        return converter.toDto(member);
//    }
}
