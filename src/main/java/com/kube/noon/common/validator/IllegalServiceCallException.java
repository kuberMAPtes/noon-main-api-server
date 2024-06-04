package com.kube.noon.common.validator;

import java.util.HashMap;
import java.util.Map;

public class IllegalServiceCallException extends IllegalArgumentException {
    private final Map<String, Object> problems;

    public IllegalServiceCallException() {
        this.problems = new HashMap<>();
    }

    public IllegalServiceCallException(String message) {
        super(message);
        this.problems = new HashMap<>();
    }

    public IllegalServiceCallException(String message, Throwable cause) {
        super(message, cause);
        this.problems = new HashMap<>();
    }

    public IllegalServiceCallException(Throwable cause) {
        super(cause);
        this.problems = new HashMap<>();
    }

    public IllegalServiceCallException(Map<String, Object> problems) {
        this.problems = problems;
    }

    public IllegalServiceCallException(String message, Map<String, Object> problems) {
        super(message);
        this.problems = problems;
    }

    public IllegalServiceCallException(String message, Throwable cause, Map<String, Object> problems) {
        super(message, cause);
        this.problems = problems;
    }

    public IllegalServiceCallException(Throwable cause, Map<String, Object> problems) {
        super(cause);
        this.problems = problems;
    }
}
