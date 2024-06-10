package com.kube.noon.member.controller;

import com.kube.noon.common.validator.IllegalServiceCallException;
import com.kube.noon.member.exception.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
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
    public ResponseEntity<Object> handleMemberExceptions(RuntimeException ex, WebRequest request) {
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
        } else {
            return HttpStatus.BAD_REQUEST;
        }
    }

    private ResponseEntity<Object> buildErrorResponse(String message, Map<String, Object> map, HttpStatus status) {
        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("message", message);
        if (map != null) {
            responseBody.put("map", map);
        }
        return ResponseEntity.status(status).body(responseBody);
    }

}
