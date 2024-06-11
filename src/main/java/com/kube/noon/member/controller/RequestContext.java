package com.kube.noon.member.controller;

import lombok.*;

@AllArgsConstructor
@ToString
@EqualsAndHashCode
@SuppressWarnings({"LombokGetterMayBeUsed", "LombokSetterMayBeUsed"})
public class RequestContext {
//    private static final ThreadLocal<String> requestId = new ThreadLocal<>();
//    private static final ThreadLocal<String> authorization = new ThreadLocal<>();
    public static String requestId = "RequestContext에서 온 requestId";
    public static String authorization = "Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJtZW1iZXJfMTAwIiwiaWF0IjoxNjI5MzYwNjYwLCJleHAiOjE2MjkzNjA3NjB9.7J9Z6Q6J9";

    public static String getRequestId() {
//        return requestId.get();
        return requestId;
    }

    public static void setRequestId(String requestId) {
//        RequestContext.requestId.set(requestId);
        RequestContext.requestId = requestId;
    }

    public static String getAuthorization() {
//        return authorization.get();
        return authorization;
    }

    public static void setAuthorization(String authorization) {
//        RequestContext.authorization.set(authorization);
        RequestContext.authorization = authorization;
    }

    public static void clear() {
//        requestId.remove();
//        authorization.remove();
    }
}
