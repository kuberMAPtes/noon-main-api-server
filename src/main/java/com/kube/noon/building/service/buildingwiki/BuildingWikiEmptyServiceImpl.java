package com.kube.noon.building.service.buildingwiki;

import com.kube.noon.building.dto.wiki.BuildingWikiEditRequestDto;
import com.kube.noon.building.dto.wiki.BuildingWikiPageResponseDto;
import com.kube.noon.building.service.BuildingWikiService;
import lombok.extern.slf4j.Slf4j;

/**
 * 다른 BuildingWikiService 구현체가 스프링 빈으로 등록되지 않을 경우, 이 객체가 등록된다.
 *
 * @author PGD
 */
@Slf4j
public class BuildingWikiEmptyServiceImpl implements BuildingWikiService {

    @Override
    public void addPage(String buildingName) {
        log.info("Wiki page of {} has been created", buildingName);
    }

    @Override
    public BuildingWikiPageResponseDto getReadPage(int buildingId) {
        log.info("Wiki page of id - {}", buildingId);
        return new BuildingWikiPageResponseDto("", "");
    }

    @Override
    public BuildingWikiPageResponseDto getEditPage(int buildingId) {
        return getReadPage(buildingId);
    }

    @Override
    public void editPage(BuildingWikiEditRequestDto content) {
        getReadPage(content.getBuildingId());
    }
}
