package com.kube.noon.member.controller;

import com.kube.noon.common.validator.IllegalServiceCallException;
import com.kube.noon.member.exception.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@ControllerAdvice(basePackages = "com.kube.noon.member.controller")
public class GlobalExceptionHandler {

    @ExceptionHandler({
            IllegalServiceCallException.class,
            MemberCreationException.class,
            MemberDeletionException.class,
            MemberNotFoundException.class,
            MemberRelationshipCreationException.class,
            MemberRelationshipNotFoundException.class,
            MemberRelationshipUpdateException.class,
            MemberSecurityBreachException.class,
            MemberUpdateException.class
    })
    public ResponseEntity<ApiResponse<Map<String,Object>>> handleMemberExceptions(RuntimeException ex, WebRequest request) {
        Map<String, Object> map = null;
        if (ex instanceof IllegalServiceCallException) {
            map = ((IllegalServiceCallException) ex).getProblems();
        } else if (ex instanceof MemberCreationException) {
            map = ((MemberCreationException) ex).getProblems();
        } else if (ex instanceof MemberDeletionException) {
            map = ((MemberDeletionException) ex).getProblems();
        } else if (ex instanceof MemberNotFoundException) {
            map = ((MemberNotFoundException) ex).getProblems();
        } else if (ex instanceof MemberRelationshipCreationException) {
            map = ((MemberRelationshipCreationException) ex).getProblems();
        } else if (ex instanceof MemberRelationshipNotFoundException) {
            map = ((MemberRelationshipNotFoundException) ex).getProblems();
        } else if (ex instanceof MemberRelationshipUpdateException) {
            map = ((MemberRelationshipUpdateException) ex).getProblems();
        } else if (ex instanceof MemberSecurityBreachException) {
            map = ((MemberSecurityBreachException) ex).getProblems();
        } else if (ex instanceof MemberUpdateException) {
            map = ((MemberUpdateException) ex).getProblems();
        }

        return buildErrorResponse(ex.getMessage(), map, determineHttpStatus(ex));
    }

    private HttpStatus determineHttpStatus(RuntimeException ex) {
        if (ex instanceof MemberNotFoundException || ex instanceof MemberRelationshipNotFoundException) {
            return HttpStatus.NOT_FOUND;
        } else if (ex instanceof MemberSecurityBreachException) {
            return HttpStatus.FORBIDDEN;
        } else if (ex instanceof IllegalServiceCallException) {
            //유효성 검사는 OK를 반환
            return HttpStatus.OK;
        } else {
            return HttpStatus.BAD_REQUEST;
        }
    }

    private ResponseEntity<ApiResponse<Map<String,Object>>> buildErrorResponse(String message, Map<String, Object> map, HttpStatus status) {
        Map<String, Object> responseBody = new HashMap<>();
        log.error("buildErrorResponse message,map,status :: "+message, map, status);
        return ResponseEntity.status(status).body(ApiResponseFactory.createErrorResponse(message,map));
    }

}
