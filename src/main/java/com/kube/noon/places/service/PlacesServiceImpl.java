package com.kube.noon.places.service;

import com.kube.noon.places.domain.Place;
import com.kube.noon.places.dto.PlaceDto;
import com.kube.noon.places.exception.PlaceNotFoundException;
import com.kube.noon.places.repository.PlacesRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * PlacesService 인터페이스의 구현체.
 *
 * @author pgd
 * @see PlacesService
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PlacesServiceImpl implements PlacesService {
    private final PlacesRepository placesRepository;

    @Override
    public List<PlaceDto> getPlaceList(String searchKeyword) {
        log.info("searchKeyword={}", searchKeyword);

        return this.placesRepository.findByPlaceName(searchKeyword)
                .stream()
                .map(PlaceDto::from)
                .toList();
    }

    @Override
    public PlaceDto getPlaceByLatLng(double latitude, double longitude) throws PlaceNotFoundException {
        log.info("latitude={}, longitude={}", latitude, longitude);
        Place findPlace = this.placesRepository.findByLatLng(latitude, longitude);
        return PlaceDto.from(findPlace);
    }
}
