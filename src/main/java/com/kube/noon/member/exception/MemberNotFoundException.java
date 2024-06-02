package com.kube.noon.member.exception;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MemberNotFoundException extends RuntimeException{
    public MemberNotFoundException(String message){
        super(message);
        log.warn(message);
    }
}
