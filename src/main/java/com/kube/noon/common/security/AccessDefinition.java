package com.kube.noon.common.security;

import java.util.List;

/**
 * 각 URI에 대한 접근을 정의한 클래스.
 *
 * @author PGD
 * @see com.kube.noon.config.WebSecurityConfig
 */
public class AccessDefinition {
    public static final List<String> WHITE_LIST = List.of(
            "/member/login",
            "/member/logout",
            "/member/kakaoLogin",
            "/member/googleLogin",
            "/member/checkPhoneNumber",
            "/member/checkPassword",
            "/member/checkNickname",
            "/member/checkMemberId",
            "/member/addMember",
            "/member/sendAuthentificationNumber",
            "/member/confirmAuthentificationNumber"
    );

    public static final List<String> ALLOWED_TO_MEMBER = List.of(
            // Setting
            "/setting/updateSetting/**",
            "/setting/getSetting/**",

            // Places
            "/places/search",

            // Feed
            "/feed/viewCutUp/**",
            "/feed/updateFeed",
            "/feed/updateFeedComment",
            "/feed/setMainFeed",
            "/feed/deleteFeedTag",
            "/feed/deleteFeedLike/{feedId}/{memberId}",
            "/feed/deleteFeedComment/{commentId}",
            "/feed/deleteFeedAttachment/{attachmentId}",
            "/feed/deleteFeed/{feedId}",
            "/feed/deleteBookmark/{feedId}/{memberId}",
            "/feed/addbookmark/{feedId}/{memberId}",
            "/feed/addFeed",
            "/feed/addFeedTag",
            "/feed/addFeedLike/{feedId}/{memberId}",
            "/feed/addFeedComment",
            "/feed/addFeedComment",
            "/feed/search",
            "/feed/getFeedListByMember",
            "/feed/getFeedListByMemberSubscription",
            "/feed/getFeedListByMemberLike",
            "/feed/getFeedListByMemberBookmark",
            "/feed/getFeedListByBuilding",
            "/feed/getFeedLikeList",
            "/feed/getFeedAttachmentList",
            "/feed/feedViewCuntByBuilding",
            "/feed/feedTagList",
            "/feed/feedCntByTag",
            "/feed/detail",
            "/feed/FeedPopularity",

            // Member
            "/member/updateProfilePhoto",
            "/member/updateProfileIntro",
            "/member/updatePhoneNumber",
            "/member/updatePassword",
            "/member/updateDajungScore",
            "/member/listMember",
            "/member/getMemberRelationshipList",
            "/member/deleteMemberRelationship/{fromId}/{toId}",
            "/member/deleteMember/{memberId}",
            "/member/addMemberRelationship",
            "/member/getMemberProfile/{fromId}/{toId}",
            "/member/getMember/{fromId}/{memberId}/",

            // Customer Support
            "/customersupport/updateReport",
            "/customersupport/getChatbotConversation",
            "/customersupport/deleteNotice",
            "/customersupport/addReport",
            "/customersupport/addReport",
            "/customersupport/addNotice",
            "/customersupport/addBlurFile",
            "/customersupport/getReportList",
            "/customersupport/getReportByReportId",
            "/customersupport/getNoticeList",
            "/customersupport/getNoticeByNoticeId",
            "/customersupport/getImageList",
            "/customersupport/getImageByAttatchmentId",
            "/customersupport/getFilteredListByAI",

            // Chatroom
            "/chatroom/addChatroom",
            "/chatroom/getMyChatrooms",
            "/chatroom/getChatroom",

            // Chat Matching
            "/chatMatching/rejectChatting",
            "/chatMatching/applyChatting",
            "/chatMatching/acceptChatting",
            "/chatMatching/newChatApplyList",
            "/chatMatching/getChatApply",

            // Building Profile
            "/buildingProfile/deleteSubscription",
            "/buildingProfile/addSubscription",
            "/buildingProfile/addSubscriptionFromSomeone",
            "/buildingProfile/getMemberSubscriptionList",
            "/buildingProfile/getBuildingProfile",
            "/buildingProfile/getBuildingFeedList"
    );
}
