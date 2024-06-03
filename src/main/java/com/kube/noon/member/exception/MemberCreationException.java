package com.kube.noon.member.exception;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MemberCreationException extends RuntimeException{
    public MemberCreationException(String message,Throwable cause){
        super(message, cause);
    }
}
