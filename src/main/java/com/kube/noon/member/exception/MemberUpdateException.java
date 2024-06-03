package com.kube.noon.member.exception;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MemberUpdateException extends RuntimeException{
    public MemberUpdateException (String message,Throwable cause){
        super(message, cause);
    }
}
