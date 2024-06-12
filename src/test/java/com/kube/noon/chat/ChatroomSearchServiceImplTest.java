//package com.kube.noon.chat
//import com.kube.noon.chat.domain.Chatroom;
//import com.kube.noon.chat.domain.ChatroomType;
//import com.kube.noon.chat.dto.ChatroomDto;
//import com.kube.noon.chat.repository.ChatroomRepository;
//import com.kube.noon.chat.service.ChatroomSearchService;
//import com.kube.noon.chat.serviceImpl.ChatroomSearchServiceImpl;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.MockitoAnnotations;
//
//import java.util.Arrays;
//import java.util.List;
//import java.util.Optional;
//
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.mockito.Mockito.when;
//
//public class ChatroomSearchServiceImplTest {
//
//    @Mock
//    private ChatroomRepository chatroomRepository;
//
//    @InjectMocks
//    private ChatroomSearchServiceImpl chatroomSearchService;
//
//    @BeforeEach
//    public void setUp() {
//        MockitoAnnotations.openMocks(this);
//    }
//
//    @Test
//    public void testGetBuildingChatroomList() throws Exception {
//        int buildingId = 1;
//        Chatroom chatroom1 = createChatroom(1, "creator1", buildingId, "Room1", ChatroomType.PUBLIC, true);
//        Chatroom chatroom2 = createChatroom(2, "creator2", buildingId, "Room2", ChatroomType.PRIVATE, true);
//
//        when(chatroomRepository.findByBuildingId(buildingId)).thenReturn(Arrays.asList(chatroom1, chatroom2));
//
//        List<ChatroomDto> result = chatroomSearchService.getBuildingChatroomList(buildingId);
//
//        assertEquals(2, result.size());
//        assertEquals("Room1", result.get(0).getChatroomName());
//        assertEquals("Room2", result.get(1).getChatroomName());
//    }
//
//    @Test
//    public void testGetBuildingChatroomListByChatroomName() throws Exception {
//        int buildingId = 1;
//        String searchKeyword = "Room";
//        Chatroom chatroom1 = createChatroom(1, "creator1", buildingId, "Room1", ChatroomType.PUBLIC, true);
//        Chatroom chatroom2 = createChatroom(2, "creator2", buildingId, "Room2", ChatroomType.PRIVATE, true);
//
//        when(chatroomRepository.findByBuildingIdAndChatroomNameContaining(buildingId, searchKeyword)).thenReturn(Arrays.asList(chatroom1, chatroom2));
//
//        List<ChatroomDto> result = chatroomSearchService.getBuildingChatroomListByChatroomName(buildingId, searchKeyword);
//
//        assertEquals(2, result.size());
//        assertEquals("Room1", result.get(0).getChatroomName());
//        assertEquals("Room2", result.get(1).getChatroomName());
//    }
//
//    @Test
//    public void testGetChatroomListByChatroomName() throws Exception {
//        String searchKeyword = "Room";
//        Chatroom chatroom1 = createChatroom(1, "creator1", 1, "Room1", ChatroomType.PUBLIC, true);
//        Chatroom chatroom2 = createChatroom(2, "creator2", 2, "Room2", ChatroomType.PRIVATE, true);
//
//        when(chatroomRepository.findByChatroomNameContaining(searchKeyword)).thenReturn(Arrays.asList(chatroom1, chatroom2));
//
//        List<ChatroomDto> result = chatroomSearchService.getChatroomListByChatroomName(searchKeyword);
//
//        assertEquals(2, result.size());
//        assertEquals("Room1", result.get(0).getChatroomName());
//        assertEquals("Room2", result.get(1).getChatroomName());
//    }
//
//    @Test
//    public void testGetLivelistChatroomList() throws Exception {
//        int buildingId = 1;
//        Chatroom chatroom1 = createChatroom(1, "creator1", buildingId, "Room1", ChatroomType.PUBLIC, true);
//        Chatroom chatroom2 = createChatroom(2, "creator2", buildingId, "Room2", ChatroomType.PRIVATE, true);
//
////        when(chatroomRepository.findByBuildingIdAndActivatedTrue(buildingId)).thenReturn(Arrays.asList(chatroom1, chatroom2));
//
//        List<ChatroomDto> result = chatroomSearchService.getLivelistChatroomList(buildingId);
//
//        assertEquals(2, result.size());
//        assertEquals("Room1", result.get(0).getChatroomName());
//        assertEquals("Room2", result.get(1).getChatroomName());
//    }
//
//    private Chatroom createChatroom(int id, String creatorId, int buildingId, String name, ChatroomType type, boolean activated) {
//        Chatroom chatroom = new Chatroom();
//        chatroom.setChatroomId(id);
//        chatroom.setChatroomCreatorId(creatorId);
//        chatroom.setBuildingId(buildingId);
//        chatroom.setChatroomName(name);
//        chatroom.setChatroomType(type);
//        chatroom.setActivated(activated);
//        return chatroom;
//    }
//}
