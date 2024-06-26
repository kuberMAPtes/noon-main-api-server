package com.kube.noon.common.validator;

import java.util.HashMap;
import java.util.Map;

/**
 * <p>만약 검증 메소드에서 예외를 던지고자 한다면 이 예외를 던지면 된다.
 * <p>어떤 파라미터가 어떻게 문제인지를 생성자의 Problems 객체로 전달할 수 있다.
 * <p>예를 들어, addMember(String memberId, String password) 라는 메소드가 있다고 하자.
 * <p>memberId에 문제가 있는 경우,
 * <p>Problems problems = new Problems();<br>
 * problems.put("memberId", "유효하지 않은 memberId");<br>
 * throw new IllegalServiceCallException(problems);<br>
 * <p>이와 같이 IllegalServiceCallException을 던질 수 있다.
 *
 * @author PGD
 * @see Problems
 * @see Validator
 */
public class IllegalServiceCallException extends IllegalArgumentException {
    private final Problems problems;

    public IllegalServiceCallException() {
        this.problems = new Problems();
    }

    public IllegalServiceCallException(String message) {
        super(message);
        this.problems = new Problems();
    }

    public IllegalServiceCallException(String message, Throwable cause) {
        super(message, cause);
        this.problems = new Problems();
    }

    public IllegalServiceCallException(Throwable cause) {
        super(cause);
        this.problems = new Problems();
    }

    public IllegalServiceCallException(Problems problems) {
        this.problems = problems;
    }

    public IllegalServiceCallException(String message, Problems problems) {
        super(message);
        this.problems = problems;
    }

    public IllegalServiceCallException(String message, Throwable cause, Problems problems) {
        super(message, cause);
        this.problems = problems;
    }

    public IllegalServiceCallException(Throwable cause, Problems problems) {
        super(cause);
        this.problems = problems;
    }

    public Problems getProblems() {
        return this.problems;
    }
}
