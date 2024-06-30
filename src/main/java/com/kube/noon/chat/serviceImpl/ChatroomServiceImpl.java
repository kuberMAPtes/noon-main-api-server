package com.kube.noon.chat.serviceImpl;

import com.kube.noon.building.domain.Building;
import com.kube.noon.building.dto.BuildingDto;
import com.kube.noon.building.service.BuildingProfileService;
import com.kube.noon.chat.domain.ChatEntrance;
import com.kube.noon.chat.domain.Chatroom;
import com.kube.noon.chat.domain.ChatroomMemberType;
import com.kube.noon.chat.domain.ChatroomType;
import com.kube.noon.chat.dto.ChatEntranceDto;
import com.kube.noon.chat.dto.ChatroomDto;
import com.kube.noon.chat.exceptions.ChatroomNotFoundException;
import com.kube.noon.chat.repository.ChatEntranceRepository;
import com.kube.noon.chat.repository.ChatroomRepository;
import com.kube.noon.chat.service.ChatroomService;
import com.kube.noon.member.domain.Member;
import com.kube.noon.member.exception.MemberNotFoundException;
import com.kube.noon.member.repository.MemberJpaRepository;
import com.kube.noon.member.service.MemberService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service("chatroomService")
@Transactional
public class ChatroomServiceImpl implements ChatroomService {

    @Autowired
    private ChatroomRepository chatroomRepository;

    @Autowired
    private ChatEntranceRepository chatEntranceRepository;

    @Autowired
    private MemberService memberService;

    @Autowired
    private BuildingProfileService buildingProfileService;

    @Autowired
    private MemberJpaRepository memberJpaRepository;

    // 채팅방 생성
    @Override
    public ChatroomDto addChatroom(ChatroomDto requestChatroom) {
        // 채팅방 저장할 chatroom entity 제작해서 저장
        Chatroom chatroom = new Chatroom();

        System.out.println("        🦐[addChatroom ServiceImpl] 채팅 생성자Id => " + requestChatroom.getChatroomCreatorId());
        chatroom.setChatroomName(requestChatroom.getChatroomName());

        // requestChatroom.getChatroomID() 로 멤버를 조회하여 멤버 ID 아닌 멤버 객체를 addChatroom 에 넣어주기
        Optional<Member> optionalMember = memberService.findMemberById(requestChatroom.getChatroomCreatorId());
        if (optionalMember.isPresent()) {
            Member member = optionalMember.get();
            System.out.println("        🦐[addChatroom ServiceImpl] memberservice 로 조회한 채팅 생성자 => " + member);
            chatroom.setChatroomCreator(member);
        } else {
            // Member를 찾지 못한 경우 처리
            System.out.println("누구세요? 신청 안받아요");
        }

        // public_chatting 요청시 addChatroom 에는 빌딩에 대한 정보도 넣어야 함
        if (requestChatroom.getChatroomType().equals(ChatroomType.GROUP_CHATTING)) {
            System.out.println("        🦐[addChatroom ServiceImpl Public Chatting] 채팅방을 세울 빌딩Id => " + requestChatroom.getBuildingId());
            Building building = new Building();
            building.setBuildingId(requestChatroom.getBuildingId());
            chatroom.setBuilding(building);

            chatroom.setChatroomMinTemp(requestChatroom.getChatroomMinTemp());
            ChatroomType roomType = ChatroomType.GROUP_CHATTING;
            chatroom.setChatroomType(roomType);
            Chatroom savedChatroom = chatroomRepository.save(chatroom);
            System.out.println("        🦐[addChatroom ServiceImpl Public Chatting] 최종 만들 chatroom Entity => " + savedChatroom);

            // 채팅 생성자가 채팅 참여 멤버에 안 들어갔누
            ChatEntrance chatEntrance = new ChatEntrance();
            chatEntrance.setChatroom(savedChatroom);

            Optional<Member> chatroomCreator = memberJpaRepository.findMemberByMemberId(requestChatroom.getChatroomCreatorId());
            if (chatroomCreator.isPresent()) {
                chatEntrance.setChatroomMember(chatroomCreator.get());
            } else {
                //
            }
            chatEntrance.setChatroomMemberType(ChatroomMemberType.OWNER);
            ChatEntrance savedChatEntrance = chatEntranceRepository.save(chatEntrance);

            System.out.println("        🦐[ServiceImpl] 생성자를 채팅 멤버에 저장 => " + savedChatEntrance);

            // Entity를 Dto로 변환해서 리턴
            return convertToChatroomDto(savedChatroom);
        }

        // private_chatting 요청시 addChatroom 에는 빌딩에 대한 정보 안 넣어줌, 다정 온도도 안 들어감
        if (requestChatroom.getChatroomType().equals(ChatroomType.PRIVATE_CHATTING)){
            System.out.println("        🦐[addChatroom ServiceImpl Private Chatting]");

            ChatroomType roomType = ChatroomType.PRIVATE_CHATTING;
            chatroom.setChatroomType(roomType);
            Chatroom savedChatroom = chatroomRepository.save(chatroom);

            // 채팅 생성자 및 피신청자도 채팅 참여 멤버에 들어가자
            ChatEntrance chatEntrance1 = new ChatEntrance();
            chatEntrance1.setChatroom(savedChatroom);

            Optional<Member> chatroomCreator1 = memberJpaRepository.findMemberByMemberId(requestChatroom.getChatroomCreatorId());
            if (chatroomCreator1.isPresent()) {
                chatEntrance1.setChatroomMember(chatroomCreator1.get());
            } else {
                //
            }
            chatEntrance1.setChatroomMemberType(ChatroomMemberType.OWNER);
            ChatEntrance savedChatEntrance1 = chatEntranceRepository.save(chatEntrance1);

            ChatEntrance chatEntrance2 = new ChatEntrance();
            chatEntrance2.setChatroom(savedChatroom);
            Optional<Member> chatroomCreator2 = memberJpaRepository.findMemberByMemberId(requestChatroom.getChatroomCreatorId());
            if (chatroomCreator2.isPresent()) {
                chatEntrance2.setChatroomMember(chatroomCreator2.get());
            } else {
                //
            }
            chatEntrance2.setChatroomMemberType(ChatroomMemberType.MEMBER);
            ChatEntrance savedChatEntrance2 = chatEntranceRepository.save(chatEntrance2);

            System.out.println("        🦐[ServiceImpl] 생성자를 채팅 멤버에 저장 => " + savedChatEntrance1 + savedChatEntrance2);

            // Entity를 Dto로 변환해서 리턴
            return convertToChatroomDto(savedChatroom);
        }

        return null;
    }


    // 채팅방입장
    @Override
    public ChatEntranceDto enterChatroom(int roomId, String memberId){

        // 채팅방 검색
        Chatroom chatroom = chatroomRepository.findChatroomByChatroomId(roomId);
        System.out.println("        🦐[enterChatroom ServiceImpl] 새로운 입장멤버를 저장할 채팅방" + chatroom);

        // 멤버 검색(?)
        Optional<Member> member = memberJpaRepository.findMemberByMemberId(memberId);
        System.out.println("        🦐[addChatroom ServiceImpl] 저장할 채팅 멤버" + member);

        // 받은 memberId를 채팅방으 entrance 에 추가
        ChatEntrance chatEntrance = new ChatEntrance();
        chatEntrance.setChatroom(chatroom);

        Optional<Member> chatroomCreator = memberJpaRepository.findMemberByMemberId(memberId);
        if (chatroomCreator.isPresent()) {
            chatEntrance.setChatroomMember(chatroomCreator.get());
        } else {
            //
        }
        chatEntrance.setChatroomMemberType(ChatroomMemberType.MEMBER);
        ChatEntrance savedChatEntrance = chatEntranceRepository.save(chatEntrance);

        return convertToChatEntranceDto(savedChatEntrance);
    }

    @Override
    public String deleteChatroom(int chatroomId) throws Exception {
        // 채팅방 삭제
        chatroomRepository.deleteChatroomByChatroomId(chatroomId);

        Chatroom chatroom = chatroomRepository.findChatroomByChatroomId(chatroomId);
        if(chatroom != null) {
            System.out.println("        🦐[ServiceImpl] deleteChatroom 삭제 안됨 => " + chatroom);
        }
        return "delete success";
    }

    @Override
    public int scheduledDeleteGroupChatrooms() throws Exception {

        return chatroomRepository.deactivateByChatroomType(ChatroomType.GROUP_CHATTING);
    }

    // 채팅방에서 참여멤버 강퇴
    @Override
    public Map<String, Object> kickChatroom(int chatroomId, String memberId) throws Exception {
        System.out.println("        🦐[ServiceImpl] getChatEntranceByChatroom requestChatroom => " + chatroomId + " " + memberId);

        // 채팅방 찾기
        Chatroom chatroom = chatroomRepository.findChatroomByChatroomId(chatroomId);
        if (chatroom == null) {
            throw new ChatroomNotFoundException("Chatroom not found");
        }
        System.out.println("findCHatroomByChatroomId 완료");

        // 해당 채팅방에서 회원 추방
        int updatedRows = chatEntranceRepository.kickMember(chatroom, memberId);
        if (updatedRows == 0){
            throw new MemberNotFoundException("Member not found or already kicked");
        }
        System.out.println("kickMember" + memberId + "내보내기 완료");

        // 추방 당하지 않은 회원 다시 불러오기
        List<ChatEntrance> activeChatEntrances = chatEntranceRepository.findByChatroomAndKickedFalse(chatroom);
        System.out.println("findByChatroomAndKickedFalse 완료");

        // 채팅방+추방당하지 않은 회원 정보 리턴
        Map<String, Object> result = new HashMap<>();
        result.put("chatroom", convertToChatroomDto(chatroom));
        result.put("activeChatEntrances", convertToChatEntranceDtoList(activeChatEntrances));
        System.out.println("result 저장 완료" + result);
        
        return result;
    }

    // 채팅방 참여멤버목록을 채팅방으로 조회
    @Override
    public List<ChatEntranceDto> getChatEntranceListByChatroom(ChatroomDto requestChatroom) {
        System.out.println("        🦐[ServiceImpl] getChatEntranceListByChatroom requestChatroom => " + requestChatroom);

        // DB에서 찾아온 채팅방
        Chatroom chatroom = chatroomRepository.findChatroomByChatroomId(requestChatroom.getChatroomID());
        System.out.println("        🦐[ServiceImpl] chatroom Repository 로 찾은 채팅방 결과 => " + chatroom);

        // 채팅방에 참여멤버 목록
        List<ChatEntrance> entrances = chatEntranceRepository.findChatEntrancesByChatroom(chatroom);
        System.out.println("        🦐[ServiceImpl] chatEntrance Repository 로 찾은 채팅멤버목록 결과 "+ entrances.size() + entrances );

        List<ChatEntranceDto> entranceDtos = new ArrayList<>();

        if (!entrances.isEmpty()) {

            for (ChatEntrance entrance : entrances) {
                ChatEntranceDto entranceDto = new ChatEntranceDto();

                entranceDto.setChatroomId(entrance.getChatroom().getChatroomId());
                entranceDto.setChatroomMember(entrance.getChatroomMember());
                entranceDto.setChatroomMemberType(entrance.getChatroomMemberType());
                entranceDto.setChatroomEnteredTime(entrance.getChatroomEnteredTime());
                entranceDto.setKicked(entrance.isKicked());

                entranceDtos.add(entranceDto);
            }
        }

        return entranceDtos;
    }

/////////////////////////////////////// 고급 JPA 코드사용 공부 필요////////////////////////////
    // 채팅방 참여멤버를 채팅방ID로 조회
    // input : chatroomId (Jpa가 join 해서 Chatroom 타입이어야함)
    // output : List<chatEntrance>
    @Override
    public List<ChatEntranceDto> getChatEntranceListbyRoomId(int chatroomID) throws Exception {

        // JPA chatEntrance Entity 의 chatroomId 가 Chatroom 과 연관관계여서 ChatroomEntity를 만들어주고 있음
        Chatroom temp = new Chatroom();
        temp.setChatroomId(chatroomID);

        // 채팅방과 엮여있는 멤버들 전부조회
        List<ChatEntrance> entrances = chatEntranceRepository.findChatEntrancesByChatroom(temp);
        List<ChatEntranceDto> result = entrances.stream()
                .map(this::convertToChatEntranceDto)
                .collect(Collectors.toList());
        System.out.println("result"+result);

        return result;
    }

/////////////////////////////////////////////////////////////////////////////////////////

    // 채팅방에 대한 정보를 채팅방ID로 조회
    @Override
    public ChatroomDto getChatroomByRoomId(int chatroomID) {
        System.out.println("        🦐[ServiceImpl] getChatroomByRoomId (chatroomId) => " + chatroomID);

        Chatroom chatroom = chatroomRepository.findChatroomByChatroomId(chatroomID);
        if (chatroom != null) {
            System.out.println("        🦐[ServiceImpl] getChatroomByRoomId return => " + convertToChatroomDto(chatroom));

            return convertToChatroomDto(chatroom);
        }
        return null;
    }



    private ChatroomDto convertToChatroomDto(Chatroom chatroom) {
        ChatroomDto dto = new ChatroomDto();
        dto.setChatroomID(chatroom.getChatroomId());
        dto.setChatroomName(chatroom.getChatroomName());
        dto.setChatroomMinTemp(chatroom.getChatroomMinTemp());
        dto.setChatroomCreatorId(chatroom.getChatroomCreator().getMemberId());
        dto.setChatroomType(chatroom.getChatroomType()); // Enum 값을 문자열로 변환하여 설정
        dto.setBuildingId(chatroom.getBuilding().getBuildingId());
        return dto;
    }

    private ChatEntranceDto convertToChatEntranceDto(ChatEntrance chatEntrance) {
        ChatEntranceDto dto = new ChatEntranceDto();
        dto.setChatEntranceId(chatEntrance.getChatEntranceId());
        dto.setChatroomId(chatEntrance.getChatroom().getChatroomId()); // chatroom 엔티티의 ID 가져오기
        dto.setChatroomMember(chatEntrance.getChatroomMember());
        dto.setChatroomMemberType(chatEntrance.getChatroomMemberType()); // Enum 값을 문자열로 변환하여 설정
        dto.setChatroomEnteredTime(chatEntrance.getChatroomEnteredTime());
        dto.setKicked(chatEntrance.isKicked());
        return dto;
    }

    public List<ChatEntranceDto> convertToChatEntranceDtoList(List<ChatEntrance> chatEntranceList) {
        return chatEntranceList.stream()
                .map(this::convertToChatEntranceDto)
                .collect(Collectors.toList());
    }
}
