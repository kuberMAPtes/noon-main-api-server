package com.kube.noon.building.controller;

import com.kube.noon.building.dto.wiki.BuildingWikiEditRequestDto;
import com.kube.noon.building.dto.wiki.BuildingWikiPageResponseDto;
import com.kube.noon.building.service.BuildingWikiService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;

@Slf4j
@RestController
@RequestMapping("/buildingWiki")
@RequiredArgsConstructor
public class BuildingWikiRestController {
    private final BuildingWikiService buildingWikiService;

    @GetMapping("/getPage/{buildingId}")
    public ResponseEntity<BuildingWikiPageResponseDto> getPage(@PathVariable("buildingId") int buildingId) {
        try {
            return ResponseEntity.ok(this.buildingWikiService.getReadPage(buildingId));
        } catch (HttpClientErrorException.NotFound e) {
            log.warn("Id{} Not found", buildingId);
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/getEditPage/{buildingId}")
    public ResponseEntity<BuildingWikiPageResponseDto> getEditPage(@PathVariable("buildingId") int buildingId) {
        try {
            return ResponseEntity.ok(this.buildingWikiService.getEditPage(buildingId));
        } catch (HttpClientErrorException.NotFound e) {
            log.warn("Id{} Not found", buildingId);
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/editPage/{buildingId}")
    public ResponseEntity<Void> editPage(
            @PathVariable("buildingId") int buildingId,
            @RequestBody BuildingWikiEditRequestDto dto
    ) {
        try {
            this.buildingWikiService.editPage(buildingId, dto);
            return ResponseEntity.ok().build();
        } catch (HttpClientErrorException.NotFound e) {
            log.warn("Id{} Not found", buildingId);
            return ResponseEntity.notFound().build();
        }
    }
}
