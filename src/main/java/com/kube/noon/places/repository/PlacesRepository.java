package com.kube.noon.places.repository;

import com.kube.noon.places.domain.Place;
import com.kube.noon.places.exception.PlaceNotFoundException;

import java.util.List;

public interface PlacesRepository {

    public List<Place> searchPlaceList(String searchKeyword);

    public List<Place> findByPlaceName(String placeName);

    public Place findByPosition(double latitude, double longitude) throws PlaceNotFoundException;
}
