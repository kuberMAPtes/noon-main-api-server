package com.kube.noon.member.exception;

import lombok.Getter;

import java.util.Map;

@Getter
public class MemberRelationshipNotFoundException extends RuntimeException {
    private Map<String, Object> problems;

    public MemberRelationshipNotFoundException(String message) {
        super(message);
    }

    public MemberRelationshipNotFoundException(String message, Map<String, Object> problems) {
        super(message);
        this.problems = problems;
    }

    public MemberRelationshipNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public MemberRelationshipNotFoundException(String message, Throwable cause, Map<String, Object> problems) {
        super(message, cause);
        this.problems = problems;
    }

}
