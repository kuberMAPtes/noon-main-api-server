

package com.kube.noon.building.controller;
import com.kube.noon.building.dto.BuildingDto;
import com.kube.noon.building.dto.BuildingSearchResponseDto;
import com.kube.noon.building.dto.BuildingZzimDto;
import com.kube.noon.building.service.BuildingProfileService;
import com.kube.noon.chat.dto.ChatroomDto;
import com.kube.noon.chat.service.ChatroomSearchService;
import com.kube.noon.feed.dto.FeedSummaryDto;
import com.kube.noon.feed.service.FeedService;
import com.kube.noon.member.dto.memberRelationship.MemberRelationshipDto;
import com.kube.noon.places.domain.Position;
import com.kube.noon.places.domain.PositionRange;
import com.kube.noon.places.exception.PlaceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/buildingProfile")
@RequiredArgsConstructor
public class BuildingProfileRestController {

    ///Field
    private final BuildingProfileService buildingProfileService;
    private final ChatroomSearchService chatroomSearchService;
    private final FeedService feedService;



    /**
     * 구독하기 or 건물 등록 신청하기
     * @return 구독에 성공하면 구독 정보 리턴(activated=true)
     */
    @PostMapping("/addSubscription")
    public BuildingZzimDto addSubscription(@RequestBody BuildingZzimDto buildingZzimDto) {
        return buildingProfileService.addSubscription(buildingZzimDto.getMemberId(), buildingZzimDto.getBuildingId());
    }

    /**
     * 구독취소하기 or 건물 등록 신청 취소하기
     * @return 구독 취소에 성공하면 구독취소 정보 리턴(activated=false)
     */
    @PostMapping("/deleteSubscription")
    public BuildingZzimDto deleteSubscription(@RequestBody BuildingZzimDto buildingZzimDto ) {
        return buildingProfileService.deleteSubscription(buildingZzimDto.getMemberId(), buildingZzimDto.getBuildingId());
    }

    /**
     * 타회원의 건물 구독 목록 가져와 회원의 구독 목록에 합치기
     * @param memberRelationshipDto fromId는 회원 아이디, toId는 타회원 아이디
     * @return 합쳐진(회원+타회원) 건물 구독 목록 리스트
     */
    @PostMapping("/addSubscriptionFromSomeone")
    public List<BuildingDto> addSubscriptionFromSomeone(@RequestBody MemberRelationshipDto memberRelationshipDto ) {
        return buildingProfileService.addSubscriptionFromSomeone(memberRelationshipDto.getFromMember().getMemberId(), memberRelationshipDto.getToMember().getMemberId());
    }

    /**
     * 회원의 건물 구독 목록 가져오기\
     */
    @GetMapping("/getMemberSubscriptionList")
    public List<BuildingDto> getMemberSubscriptionList(@RequestParam("memberId") String memberId) {
        return buildingProfileService.getMemberBuildingSubscriptionList(memberId);
    }

    /**
     * 건물의 피드 목록 가져오기
     */
    @GetMapping("/getBuildingFeedList")
    public List<FeedSummaryDto> getBuildingFeedList(@RequestParam("buildingId") int buildingId) {
        return feedService.getFeedListByBuilding(buildingId);
    }

    /**
     * 건물의 채팅 목록 가져오기
     */
    @GetMapping("/getBuildingChatroomList")
    public List<ChatroomDto> getBuildingChatroomList(@RequestParam("buildingId") int buildingId) throws Exception {
        return chatroomSearchService.getBuildingChatroomList(buildingId);
    }


    /**
     * 건물의 프로필 정보 가져오기
     */
    @GetMapping(value = "/getBuildingProfile", params = "buildingId")
    public BuildingDto getBuildingProfile(@RequestParam("buildingId") int buildingId) {
        return buildingProfileService.getBuildingProfile(buildingId);
    }

    @GetMapping(value = "/getBuildingProfile", params = { "latitude", "longitude" })
    public ResponseEntity<Object> getBuildingProfile(@ModelAttribute Position position) {
        try {
            BuildingDto resp = this.buildingProfileService.getBuildingProfileByPosition(position);
            return new ResponseEntity<>(resp, HttpStatus.OK);
        } catch (PlaceNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }


    /**
     * 사용자의 화면 범위 내 건물 목록 보기
     */
    @GetMapping("/getBuildingsWithinRange")
    public List<BuildingDto> getBuildingsWithinRange(@ModelAttribute PositionRange positionRange){

        return  buildingProfileService.getBuildingsWithinRange(positionRange);
    }


    @GetMapping("/searchBuilding")
    public ResponseEntity<List<BuildingSearchResponseDto>> searchBuilding(
            @RequestParam("searchKeyword") String searchKeyword,
            @RequestParam(value = "page", required = false) Integer page
    ) {
        return new ResponseEntity<>(this.buildingProfileService.searchBuilding(searchKeyword, page), HttpStatus.OK);
    }

}
