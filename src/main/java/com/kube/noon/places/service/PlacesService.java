package com.kube.noon.places.service;

import com.kube.noon.places.dto.PlaceDto;
import com.kube.noon.places.exception.PlaceNotFoundException;
import com.kube.noon.places.repository.PlacesNaverMapsApiRepositoryImpl;

import java.util.List;

/**
 * <p>장소 검색 비즈니스 로직을 정의한 Service interface</p>
 * <p>장소 검색 키워드 => 실제 장소의 도로명주소, 위도 및 경도
 * 위도 및 경도 => 해당 위도 및 경도에 위치한 실제 장소의 도로명주소, 위도 및 경도</p>
 *
 * @author pgd
 * @see com.kube.noon.places.repository.PlacesRepository
 * @see PlacesNaverMapsApiRepositoryImpl
 * @see com.kube.noon.places.domain.Place
 * @see PlaceDto
 * @see PlacesServiceImpl
 */
public interface PlacesService {

    /**
     * 주어진 키워드로부터 장소를 얻는다.
     * @param searchKeyword 장소 검색 키워드
     * @return searchKeyword에 부합하는 장소들을 반환. 반환되는 List의 길이는 1보다 클 수 있고, 1일 수 있고, 0일 수 있다.
     */
    public List<PlaceDto> getPlaceList(String searchKeyword);

    /**
     * 주어진 위도, 경도에 위치한 장소 정보를 얻는다.
     * @param latitude 위도값. 33~43 사이의 실수여야 한다.
     * @param longitude 경도값. 124~132 사이의 실수여야 한다.
     * @return latitude, longitude에 위치한 장소의 정보
     * @throws PlaceNotFoundException 주어진 latitude, longitude에 위치한 장소가 없을 경우
     */
    public PlaceDto getPlaceByPosition(double latitude, double longitude) throws PlaceNotFoundException;
}