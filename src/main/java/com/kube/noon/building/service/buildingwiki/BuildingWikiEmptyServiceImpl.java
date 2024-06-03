package com.kube.noon.building.service.buildingwiki;

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
    public void addPage(String title) {
        log.info("Wiki page of {} has been created", title);
    }
}
