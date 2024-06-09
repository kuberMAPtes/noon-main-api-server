package com.kube.noon.member.exception;

import lombok.Getter;

import java.util.Map;

@Getter
public class MemberNotFoundException extends RuntimeException {
    private Map<String, Object> problems;

    public MemberNotFoundException(String message) {
        super(message);
    }

    public MemberNotFoundException(String message, Map<String, Object> problems) {
        super(message);
        this.problems = problems;
    }

    public MemberNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public MemberNotFoundException(String message, Throwable cause, Map<String, Object> problems) {
        super(message, cause);
        this.problems = problems;
    }

}
