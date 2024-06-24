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

    @GetMapping("/getPage")
    public ResponseEntity<BuildingWikiPageResponseDto> getPage(@RequestParam("buildingId") int buildingId) {
        return ResponseEntity.ok(this.buildingWikiService.getReadPage(buildingId));
    }

    @GetMapping("/getEditPage")
    public ResponseEntity<BuildingWikiPageResponseDto> getEditPage(@RequestParam("buildingId") int buildingId) {
        return ResponseEntity.ok(this.buildingWikiService.getEditPage(buildingId));
    }

    @PostMapping("/editPage")
    public ResponseEntity<Void> editPage(@ModelAttribute BuildingWikiEditRequestDto dto) {
        this.buildingWikiService.editPage(dto);
        return ResponseEntity.ok().build();
    }
}
