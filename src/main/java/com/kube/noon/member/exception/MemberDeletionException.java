package com.kube.noon.member.exception;

import lombok.Getter;

import java.util.Map;

@Getter
public class MemberDeletionException extends RuntimeException {
    private Map<String, Object> problems;

    public MemberDeletionException(String message) {
        super(message);
    }

    public MemberDeletionException(String message, Map<String, Object> problems) {
        super(message);
        this.problems = problems;
    }

    public MemberDeletionException(String message, Throwable cause) {
        super(message, cause);
    }

    public MemberDeletionException(String message, Throwable cause, Map<String, Object> problems) {
        super(message, cause);
        this.problems = problems;
    }

}
