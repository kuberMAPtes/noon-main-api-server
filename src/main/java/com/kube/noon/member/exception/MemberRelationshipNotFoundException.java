package com.kube.noon.member.exception;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MemberRelationshipNotFoundException extends RuntimeException{
    public MemberRelationshipNotFoundException(String message){
        super(message);
    }
}
