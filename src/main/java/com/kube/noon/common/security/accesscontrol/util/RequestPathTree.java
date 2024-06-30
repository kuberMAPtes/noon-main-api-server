package com.kube.noon.common.security.accesscontrol.util;

import lombok.ToString;
import org.springframework.http.HttpMethod;

import java.util.Optional;

@ToString
public class RequestPathTree<E> {
    private final RequestPathNode<E> root;

    public RequestPathTree() {
        this.root = new RequestPathNode<>();
    }

    public void add(String path, HttpMethod method, E element) {
        this.root.add(path, method, element);
    }

    public Optional<E> getElement(String path, HttpMethod method) {
        RequestPathNode<E> node = this.root.getNode(path);
        if (node == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(node.getElementPerMethod().get(method));
    }
}
