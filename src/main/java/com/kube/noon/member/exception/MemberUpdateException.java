package com.kube.noon.member.exception;

import lombok.Getter;

import java.util.Map;

@Getter
public class MemberUpdateException extends RuntimeException {
    private Map<String, Object> problems;

    public MemberUpdateException(String message) {
        super(message);
    }

    public MemberUpdateException(String message, Map<String, Object> problems) {
        super(message);
        this.problems = problems;
    }

    public MemberUpdateException(String message, Throwable cause) {
        super(message, cause);
    }

    public MemberUpdateException(String message, Throwable cause, Map<String, Object> problems) {
        super(message, cause);
        this.problems = problems;
    }

}
