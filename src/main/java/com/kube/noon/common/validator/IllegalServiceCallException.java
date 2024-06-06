package com.kube.noon.common.validator;

import java.util.HashMap;
import java.util.Map;

/**
 * 만약 검증 메소드에서 예외를 던지고자 한다면 이 예외를 던지면 된다.
 * 어떤 파라미터가 어떻게 문제인지를 생성자의 Map으로 전달할 수 있다.
 *
 * @author PGD
 */
public class IllegalServiceCallException extends IllegalArgumentException {
    private final Map<String, Object> problems;

    public IllegalServiceCallException() {
        this.problems = new HashMap<>();
    }

    public IllegalServiceCallException(String message) {
        super(message);
        this.problems = new HashMap<>();
    }

    public IllegalServiceCallException(String message, Throwable cause) {
        super(message, cause);
        this.problems = new HashMap<>();
    }

    public IllegalServiceCallException(Throwable cause) {
        super(cause);
        this.problems = new HashMap<>();
    }

    public IllegalServiceCallException(Map<String, Object> problems) {
        this.problems = problems;
    }

    public IllegalServiceCallException(String message, Map<String, Object> problems) {
        super(message);
        this.problems = problems;
    }

    public IllegalServiceCallException(String message, Throwable cause, Map<String, Object> problems) {
        super(message, cause);
        this.problems = problems;
    }

    public IllegalServiceCallException(Throwable cause, Map<String, Object> problems) {
        super(cause);
        this.problems = problems;
    }

    public Map<String, Object> getProblems() {
        return this.problems;
    }
}