

package com.kube.noon.building.controller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/buildingProfile")
public class BuildingProfileRestController {

/*
    ///Field
    @Autowired
    @Qualifier("buildingProfileServiceImpl")
    private BuildingPrfileService buildingProfileService;


    @Autowired
    @Qualifier("chatroomServiceImpl")
    private ChatroomService chatroomService;

    @Autowired
    @Qualifier("feedServiceImpl")
    private FeedService feedService;
    */


    /*
    @PostMapping("/addSubscription")
    public void addSubscription(@RequestBody SubscriptionRequest subscriptionRequest) {
        buildingProfileService.addSubscription(subscriptionRequest);
    }

    @PostMapping("/deleteSubscription")
    public void deleteSubscription(@RequestBody SubscriptionRequest subscriptionRequest) {
        buildingProfileService.deleteSubscription(subscriptionRequest);
    }

     */

    /*
    //FeedService를 pull한 후 활용 예정
    @GetMapping("/getBuildingFeedList")
    public List<Feed> getBuildingFeedList(@RequestParam("buildingId") String buildingId) {
        return feedService.getBuildingFeedList(buildingId);
    }

    //ChatService pull한 후 활용 예정
    @GetMapping("/getBuildingChatroomList")
    public List<Chatroom> getBuildingChatroomList(@RequestParam("buildingId") String buildingId) {
        return chatroomService.getBuildingChatroomList(buildingId);
    }
    */
}
