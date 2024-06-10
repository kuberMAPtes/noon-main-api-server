package com.kube.noon.member.exception;

import lombok.Getter;

import java.util.Map;

@Getter
public class MemberSecurityBreachException extends RuntimeException {
    private Map<String, Object> problems;

    public MemberSecurityBreachException(String message) {
        super(message);
    }

    public MemberSecurityBreachException(String message, Map<String, Object> problems) {
        super(message);
        this.problems = problems;
    }

    public MemberSecurityBreachException(String message, Throwable cause) {
        super(message, cause);
    }

    public MemberSecurityBreachException(String message, Throwable cause, Map<String, Object> problems) {
        super(message, cause);
        this.problems = problems;
    }

}
