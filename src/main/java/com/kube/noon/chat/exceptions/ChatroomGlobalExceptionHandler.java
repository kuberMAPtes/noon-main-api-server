package com.kube.noon.chat.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@ControllerAdvice(basePackages = "com.kube.noon.chat.controller")
public class ChatroomGlobalExceptionHandler {

    @ExceptionHandler(ChatroomNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<Map<String, Object>> handleChatroomNotFoundException(ChatroomNotFoundException ex) {
        // 예외를 로그로 기록
        System.err.println("Chatroom not found: " + ex.getMessage());

        // 응답 객체 생성
        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("timestamp", LocalDateTime.now());
        responseBody.put("status", HttpStatus.NOT_FOUND.value());
        responseBody.put("error", "Not Found");
        responseBody.put("message", ex.getMessage());

        // chatroomNotFoundException 이 발생했을 때 404 NotFound 와 함께
        return new ResponseEntity<>(responseBody, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(ChatroomAutoDeleteFailException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<Map<String, Object>> handleChatroomAutoDeleteFaileException(ChatroomAutoDeleteFailException ex) {
        // 예외를 로그로 기록
        System.err.println("Chatroom not found: " + ex.getMessage());

        // 응답 객체 생성
        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("timestamp", LocalDateTime.now());
        responseBody.put("status", HttpStatus.NOT_FOUND.value());
        responseBody.put("error", "Not Found");
        responseBody.put("message", ex.getMessage());

        // chatroomNotFoundException 이 발생했을 때 404 NotFound 와 함께
        return new ResponseEntity<>(responseBody, HttpStatus.NOT_FOUND);
    }
}