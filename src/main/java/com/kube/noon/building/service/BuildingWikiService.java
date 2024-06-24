package com.kube.noon.building.service;

import com.kube.noon.building.dto.wiki.BuildingWikiEditRequestDto;
import com.kube.noon.building.dto.wiki.BuildingWikiPageResponseDto;

/**
 * 건물 위키 로직을 처리하는 Service 인터페이스
 *
 * @author PGD
 */
public interface BuildingWikiService {

    /**
     * 빈 위키 페이지를 생성한다. title에 해당하는 페이지가 이미 있을 경우, 페이지를 생성하지 않는다.
     * @param buildingName 생성할 건물 위키 페이지의 제목
     */
    public void addPage(int buildingId);

    public BuildingWikiPageResponseDto getReadPage(int buildingId);

    public BuildingWikiPageResponseDto getEditPage(int buildingId);

    public void editPage(BuildingWikiEditRequestDto dto);
}
