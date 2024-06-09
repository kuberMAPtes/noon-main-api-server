package com.kube.noon.member.exception;

import lombok.Getter;

import java.util.Map;

@Getter
public class MemberRelationshipUpdateException extends RuntimeException {
    private Map<String, Object> problems;

    public MemberRelationshipUpdateException(String message) {
        super(message);
    }

    public MemberRelationshipUpdateException(String message, Map<String, Object> problems) {
        super(message);
        this.problems = problems;
    }

    public MemberRelationshipUpdateException(String message, Throwable cause) {
        super(message, cause);
    }

    public MemberRelationshipUpdateException(String message, Throwable cause, Map<String, Object> problems) {
        super(message, cause);
        this.problems = problems;
    }

}
