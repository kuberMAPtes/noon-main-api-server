package com.kube.noon.member.exception;

import lombok.Getter;

import java.util.Map;

@Getter
public class MemberRelationshipCreationException extends RuntimeException {
    private Map<String, Object> problems;

    public MemberRelationshipCreationException(String message) {
        super(message);
    }

    public MemberRelationshipCreationException(String message, Map<String, Object> problems) {
        super(message);
        this.problems = problems;
    }

    public MemberRelationshipCreationException(String message, Throwable cause) {
        super(message, cause);
    }

    public MemberRelationshipCreationException(String message, Throwable cause, Map<String, Object> problems) {
        super(message, cause);
        this.problems = problems;
    }

}
