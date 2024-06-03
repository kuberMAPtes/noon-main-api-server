package com.kube.noon.building.service;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class BuildingWikiEmptyServiceImpl implements BuildingWikiService {

    @Override
    public void addPage(String title) {
        log.info("Wiki page of {} has been created", title);
    }
}
