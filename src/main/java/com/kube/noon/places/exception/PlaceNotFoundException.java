package com.kube.noon.places.exception;

public class PlaceNotFoundException extends Exception {

    public PlaceNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public PlaceNotFoundException(Throwable cause) {
        super(cause);
    }
}
