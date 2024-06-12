package com.kube.noon.member.controller;

import java.util.Map;

public class ApiResponseFactory {

    public static <T> ApiResponse<T> createResponse(String message, T data) {
        return new ApiResponse<>(message, data);
    }

    public static <T> ApiResponse<T> createErrorResponse(String message) {
        return new ApiResponse<>(message, null);
    }
    public static <T> ApiResponse<T> createErrorResponse(String message, T errors){
        return new ApiResponse<>(message, errors);
    }
}
