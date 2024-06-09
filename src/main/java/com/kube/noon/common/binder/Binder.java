package com.kube.noon.common.binder;


/**
 *  (맵스트럭트)매퍼 인터페이스를 만들시에 extends Binder<Dto클래스명,Entity명>으로 인터페이스를 확장한다.
 *  그 다음 @Mapper(componentModel = "spring") 어노테이션을 붙여준다.
 *  매퍼인터페이스의 구현체를 스프링의 빈에 등록하는 과정이기 때문에
 *  이 어노테이션이 없으면 DtoEntityConverter가 작동할 때 다음과 같은 에러가 발생.
 *   java.lang.IllegalArgumentException: 지원되지 않는 엔티티 타입입니다:
 *   자세한 사용법은 DtoEntityBinder Enum클래스를 참고하고, 예시 매퍼인터페이스를 참고하여 사용하면 된다.
 * @param <D>
 * @param <E>
 */
public interface Binder<D, E> {
    default E toEntity(D dto){throw new UnsupportedOperationException("Not implemented yet");};
    default E toResponseDto(D dto){throw new UnsupportedOperationException("Not implemented yet");};
    default D toDto(E entity){throw new UnsupportedOperationException("Not implemented yet");};

}
