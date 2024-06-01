package com.kube.noon.places.repository;

import com.kube.noon.places.domain.Place;
import com.kube.noon.places.exception.PlaceNotFoundException;

import java.util.List;

public interface PlacesRepository {

    List<Place> findByPlaceName(String placeName);

    public Place findByLatLng(double latitude, double longitude) throws PlaceNotFoundException;
}
