package com.kube.noon.member.exception;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MemberSecurityBreachException extends RuntimeException{
    public MemberSecurityBreachException(String message) {
        super(message);
    }
}
