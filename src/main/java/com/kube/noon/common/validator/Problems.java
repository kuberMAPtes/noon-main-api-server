package com.kube.noon.common.validator;

import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

/**
 * <p>검증 로직에서 각 문제가 있는 부분을 저장하는 객체
 * <p>IllegalServiceCallException에 저장된다.
 * <p>Key에는 문제가 있는 Parameter의 이름이 담길 수 있고
 * Value에는 아무 객체가 올 수 있으나 보통 String 객체 (검증 실패 메시지)가 담긴다.
 *
 * @author PGD
 * @see IllegalServiceCallException
 * @see Validator
 */
@NoArgsConstructor
public class Problems extends HashMap<String, Object> {

    public Problems(Map<String, Object> problems) {
        super(problems);
    }

    public static void checkProblems(Problems problems) {
        checkProblems(problems, "");
    }

    public static void checkProblems(Problems problems, Class<?> cls) {
        checkProblems(problems, "Problem in validation in " + cls);
    }

    public static void checkProblems(Problems problems, String message) {
        if (isAnyProblem(problems)) {
            throw new IllegalServiceCallException(message, problems);
        }
    }

    private static boolean isAnyProblem(Problems problems) {
        // "~이 아니다"라는 논리기 때문에 다소 가독성이 떨어짐
        // 그래서 따로 메소드로 빼 놓음으로써 의미를 주었다.
        return !problems.isEmpty();
    }
}
