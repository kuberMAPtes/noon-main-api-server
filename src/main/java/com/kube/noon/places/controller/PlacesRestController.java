package com.kube.noon.places.controller;

import com.kube.noon.places.dto.PlaceDto;
import com.kube.noon.places.exception.PlaceNotFoundException;
import com.kube.noon.places.service.PlacesService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/places")
@RequiredArgsConstructor
public class PlacesRestController {
    private final PlacesService placesService;

    @GetMapping(value = "/search", params = "placeName")
    public ResponseEntity<List<PlaceDto>> searchPlace(@RequestParam("placeName") String placeName) {
        return new ResponseEntity<>(this.placesService.getPlaceList(placeName), HttpStatus.OK);
    }

    @GetMapping(value = "/search", params = "!placeName")
    public ResponseEntity<PlaceDto> searchPlace(@RequestParam("latitude") double latitude,
                                                @RequestParam("longitude") double longitude) throws PlaceNotFoundException {
        return new ResponseEntity<>(this.placesService.getPlaceByPosition(latitude, longitude), HttpStatus.OK);
    }
}
