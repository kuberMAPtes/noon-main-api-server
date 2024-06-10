package com.kube.noon.member.exception;

import lombok.Getter;

import java.util.Map;

@Getter
public class MemberCreationException extends RuntimeException {
    private Map<String, Object> problems;

    public MemberCreationException(String message) {
        super(message);
    }

    public MemberCreationException(String message, Map<String, Object> problems) {
        super(message);
        this.problems = problems;
    }

    public MemberCreationException(String message, Throwable cause) {
        super(message, cause);
    }

    public MemberCreationException(String message, Throwable cause, Map<String, Object> problems) {
        super(message, cause);
        this.problems = problems;
    }

}