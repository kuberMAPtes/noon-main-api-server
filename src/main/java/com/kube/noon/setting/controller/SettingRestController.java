package com.kube.noon.setting.controller;

import com.kube.noon.common.validator.IllegalServiceCallException;
import com.kube.noon.common.validator.Problems;
import com.kube.noon.setting.dto.SettingDto;
import com.kube.noon.setting.service.SettingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Setting", description = "사용자 환경설정 API")
@RestController
@RequestMapping("/setting")
@RequiredArgsConstructor
public class SettingRestController {
    private final SettingService settingService;

    @Operation(summary = "회원 환경설정 업데이트", description = "회원의 환경설정을 업데이트합니다.")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "업데이트 성공"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "유효하지 않은 파라미터"
            )
    })
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

    @Operation(summary = "회원 환경설정 조회", description = "회원의 환경설정을 조회합니다.")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "회원 환경설정 정보 조회"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "유효하지 않은 파라미터"
            )
    })
    @GetMapping("/getSetting/{memberId}")
    public ResponseEntity<Object> getSetting(@PathVariable("memberId") String memberId) {
        try {
            return new ResponseEntity<>(this.settingService.findSettingOfMember(memberId), HttpStatus.OK);
        } catch (IllegalServiceCallException e) {
            return new ResponseEntity<>(e.getProblems(), HttpStatus.BAD_REQUEST);
        }
    }
}
