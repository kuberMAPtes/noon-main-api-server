package com.kube.noon.places.controller;

import com.kube.noon.places.dto.PlaceDto;
import com.kube.noon.places.exception.PlaceNotFoundException;
import com.kube.noon.places.service.PlacesService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "Places", description = "장소 검색 API")
@RestController
@RequestMapping("/places")
@RequiredArgsConstructor
public class PlacesRestController {
    private final PlacesService placesService;

    @Operation(summary = "장소명으로 장소 검색", description = "주어진 장소명에 해당하는 장소들을 조회합니다.")
    @ApiResponse(responseCode = "200", description = "장소 검색 결과")
    @GetMapping(value = "/search", params = "placeName")
    public ResponseEntity<List<PlaceDto>> searchPlace(@RequestParam("placeName") String placeName) {
        return new ResponseEntity<>(this.placesService.getPlaceList(placeName), HttpStatus.OK);
    }

    @Operation(summary = "위도, 경도로 장소 검색", description = "위도, 경도 좌표에 해당하는 장소들을 조회합니다.")
    @ApiResponse(responseCode = "200", description = "장소 검색 결과")
    @GetMapping(value = "/search", params = "!placeName")
    public ResponseEntity<PlaceDto> searchPlace(@RequestParam("latitude") double latitude,
                                                @RequestParam("longitude") double longitude) throws PlaceNotFoundException {
        return new ResponseEntity<>(this.placesService.getPlaceByPosition(latitude, longitude), HttpStatus.OK);
    }
}
