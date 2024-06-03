package com.kube.noon.member.exception;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MemberRelationshipCreationException extends RuntimeException{
    public MemberRelationshipCreationException(String message,Throwable cause){
        super(message, cause);
    }
}
