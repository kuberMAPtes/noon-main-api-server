package com.kube.noon.chat.repository;

import com.kube.noon.chat.domain.Chatroom;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChatroomRepository extends JpaRepository<Chatroom, Integer> {
    // 기본적인 CRUD 메서드는 JpaRepository가 제공
    // 추가적인 커스텀 메서드를 정의할 수 있음


    //List<Chatroom> findByBuildingId(int buildingId);
    //List<Chatroom> findByBuildingIdAndChatroomNameContaining(int buildingId, String searchKeywordChatroom);

    // chatroom entity 를 building 과 join 관계 만들면서 바뀌었어용 (buildingId 칼럼만 있었는데 Building buliding 으로 빌딩객체(?) 넣게됨)
    List<Chatroom> findByBuilding_BuildingId(int buildingId);
    List<Chatroom> findByBuildingBuildingIdAndChatroomNameContaining(int buildingId, String searchKeywordChatroom);


    List<Chatroom> findByChatroomNameContaining(String searchKeywordChatroom);
    void deleteChatroomByChatroomId(int chatroomId);
    Chatroom findChatroomByChatroomId(int chatroomId);
}
