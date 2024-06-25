package com.kube.noon.building.exception;

import com.kube.noon.places.dto.PlaceDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class NotRegisteredBuildingException extends Exception {
    private final PlaceDto place;

    public NotRegisteredBuildingException(String message, PlaceDto place) {
        super(message);
        this.place = place;
    }

    public NotRegisteredBuildingException(String message, Throwable cause, PlaceDto place) {
        super(message, cause);
        this.place = place;
    }

    public NotRegisteredBuildingException(Throwable cause, PlaceDto place) {
        super(cause);
        this.place = place;
    }
}
