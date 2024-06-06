package com.kube.noon.common.validator;

import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

@NoArgsConstructor
public class Problems extends HashMap<String, Object> {

    public Problems(Map<String, Object> problems) {
        super(problems);
    }
}
