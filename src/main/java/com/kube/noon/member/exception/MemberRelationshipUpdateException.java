package com.kube.noon.member.exception;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MemberRelationshipUpdateException extends RuntimeException{
    public MemberRelationshipUpdateException (String message,Throwable cause){
        super(message, cause);
    }
}
