package com.kube.noon.setting.controller;

import com.kube.noon.common.validator.IllegalServiceCallException;
import com.kube.noon.common.validator.Problems;
import com.kube.noon.setting.dto.SettingDto;
import com.kube.noon.setting.service.SettingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/setting")
@RequiredArgsConstructor
public class SettingRestController {
    private final SettingService settingService;

    @PostMapping("/updateSetting/{memberId}")
    public ResponseEntity<Problems> updateSetting(@PathVariable("memberId") String memberId,
                                                  @RequestBody SettingDto requestDto) {
        try {
            this.settingService.updateSetting(memberId, requestDto);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (IllegalServiceCallException e) {
            return new ResponseEntity<>(e.getProblems(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/getSetting/{memberId}")
    public ResponseEntity<Object> getSetting(@PathVariable("memberId") String memberId) {
        try {
            return new ResponseEntity<>(this.settingService.findSettingOfMember(memberId), HttpStatus.OK);
        } catch (IllegalServiceCallException e) {
            return new ResponseEntity<>(e.getProblems(), HttpStatus.BAD_REQUEST);
        }
    }
}
