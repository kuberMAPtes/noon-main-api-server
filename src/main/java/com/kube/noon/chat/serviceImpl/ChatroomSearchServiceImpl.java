//package com.kube.noon.chat.serviceImpl;
//
//import com.kube.noon.chat.domain.ChatEntrance;
//import com.kube.noon.chat.domain.Chatroom;
//import com.kube.noon.chat.dto.ChatroomDto;
//import com.kube.noon.chat.dto.ChatroomSearchResponseDto;
//import com.kube.noon.chat.repository.ChatEntranceRepository;
//import com.kube.noon.chat.repository.ChatroomRepository;
//import com.kube.noon.chat.service.ChatroomSearchService;
//import com.kube.noon.common.constant.PagingConstants;
//import jakarta.transaction.Transactional;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.PageRequest;
//import org.springframework.stereotype.Service;
//
//import java.util.List;
//import java.util.stream.Collectors;
//
//@Service("chatroomSearchService")
//@Transactional
//public class ChatroomSearchServiceImpl implements ChatroomSearchService {
//
//    @Autowired
//    private ChatroomRepository chatroomRepository;
//
//    @Autowired
//    private ChatEntranceRepository chatEntranceRepository;
//
//    @Override
//    public List<ChatroomDto> getBuildingChatroomList(int buildingId) throws Exception {
//        List<Chatroom> chatrooms = chatroomRepository.findByBuilding_BuildingId(buildingId);
//
//        List<ChatroomDto> chatroomDtos = convertToChatroomDtoList(chatrooms);
//
//        return chatroomDtos;
//    }
//
//    @Override
//    public List<ChatroomDto> getBuildingChatroomListByChatroomName(int buildingId, String searchKeywordChatroom) throws Exception {
//        List<Chatroom> chatrooms = chatroomRepository.findByBuildingBuildingIdAndChatroomNameContaining(buildingId, searchKeywordChatroom);
//
//        List<ChatroomDto> chatroomDtos = convertToChatroomDtoList(chatrooms);
//
//        return chatroomDtos;
//    }
//
//    // 채팅방 목록을 회원ID로 조회
//    @Override
//    public List<ChatroomDto> getChatroomListByMemberId(String chatroomMemberId) {
//
//        // ChatEntrance 목록을 조회하여 memberID가 입장한 채팅방을 체크
//        List<ChatEntrance> entrances = chatEntranceRepository.getChatEntrancesByChatroomMemberId(chatroomMemberId);
//
//        // 입장한 채팅방을 기준으로 채팅방에 대한 상세한 정보 Chatroom list를 얻음
//        List<Chatroom> chatrooms = entrances.stream()
//                .map(ChatEntrance::getChatroom)
//                .collect(Collectors.toList());
//
//        // ChatroomList 를 Dto 로 변환후 return
//        return chatrooms.stream()
//                .map(this::convertToChatroomDto)
//                .collect(Collectors.toList());
//    }
//
//    @Override
//    public List<ChatroomDto> getChatroomListByChatroomName(String searchKeywordChatroom) throws Exception {
//        List<Chatroom> chatrooms = chatroomRepository.findByChatroomNameContaining(searchKeywordChatroom);
//
//        List<ChatroomDto> chatroomDtos = convertToChatroomDtoList(chatrooms);
//
//        return chatroomDtos;
//    }
//
//    @Override
//    public Page<ChatroomSearchResponseDto> searchChatroomByChatroomName(String searchKeyword, int page) {
//      return this.chatroomRepository.findByChatroomNameContaining(searchKeyword, PageRequest.of(page - 1, PagingConstants.PAGE_SIZE))
//                .map(ChatroomSearchResponseDto::of);
//    }
//
//    @Override
//    public List<ChatroomDto> getLivelistChatroomList(int buildingId) throws Exception {
//        List<Chatroom> chatrooms = chatroomRepository.findByBuilding_BuildingId(buildingId);
//        List<ChatroomDto> chatroomDtos = convertToChatroomDtoList(chatrooms);
//
//        return chatroomDtos; // 구현 고민중
//    }
//
//
//    private List<ChatroomDto> convertToChatroomDtoList(List<Chatroom> chatrooms) {
//        return chatrooms.stream().map(this::convertToChatroomDto).collect(Collectors.toList());
//    }
//
//    private ChatroomDto convertToChatroomDto(Chatroom chatroom) {
//        ChatroomDto dto = new ChatroomDto();
//
//        dto.setChatroomCreator(chatroom.getChatroomCreator());
//        dto.setChatroomID(chatroom.getChatroomId());
//        dto.setChatroomName(chatroom.getChatroomName());
//        dto.setChatroomMinTemp(chatroom.getChatroomMinTemp());
//        dto.setChatroomCreator(chatroom.getChatroomCreator());
//        dto.setChatroomType(chatroom.getChatroomType().toString()); // Enum 값을 문자열로 변환하여 설정
//        return dto;
//    }
//
//}
