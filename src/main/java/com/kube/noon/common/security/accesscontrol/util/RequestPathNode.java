package com.kube.noon.common.security.accesscontrol.util;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import org.springframework.http.HttpMethod;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

@RequiredArgsConstructor
@Getter
@ToString
public class RequestPathNode<E> {
    private static final Pattern PATH_VARIABLE_PATTERN = Pattern.compile("\\{[a-zA-Z0-9]*}");
    private static final String WILDCARD = "PATH_VARIABLE";

    private final Map<String, RequestPathNode<E>> children = new HashMap<>();
    private final Map<HttpMethod, E> elementPerMethod = new HashMap<>();

    public void add(String path, HttpMethod method, E element) {
        String[] split = Arrays.stream(path.split("/")).filter((s) -> !s.isEmpty()).toArray(String[]::new);
        if (split.length == 0) {
            this.elementPerMethod.put(method, element);
            return;
        }
        String currentPath = split[0];
        String subPath = String.join("/", Arrays.copyOfRange(split, 1, split.length));

        RequestPathNode<E> child = getChild(currentPath);
        if (child == null) {
            RequestPathNode<E> newChild = new RequestPathNode<>();
            newChild.add(subPath, method, element);
            this.children.put(getWildcard(currentPath), newChild);
        } else {
            child.add(subPath, method, element);
        }
    }

    private RequestPathNode<E> getChild(String key) {
        return PATH_VARIABLE_PATTERN.matcher(key).matches()
                ? this.children.get(WILDCARD)
                : this.children.get(key);
    }

    private String getWildcard(String key) {
        return PATH_VARIABLE_PATTERN.matcher(key).matches()
                ? WILDCARD
                : key;
    }

    public RequestPathNode<E> getNode(String path) {
        String[] split = Arrays.stream(path.split("/")).filter((s) -> !s.isEmpty()).toArray(String[]::new);
        if (split.length == 0) {
            return this;
        }
        String currentPath = split[0];
        String subPath = String.join("/", Arrays.copyOfRange(split, 1, split.length));

        RequestPathNode<E> node = extractNode(currentPath);
        return node != null ? node.getNode(subPath) : null;
    }

    private RequestPathNode<E> extractNode(String key) {
        return this.children.containsKey(key) ? this.children.get(key) : this.children.get(WILDCARD);
    }
}
