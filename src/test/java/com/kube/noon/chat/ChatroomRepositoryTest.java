//package com.kube.noon.chat;
//
//import com.kube.noon.chat.domain.Chatroom;
//import com.kube.noon.chat.domain.ChatroomType;
//import com.kube.noon.chat.repository.ChatroomRepository;
//import com.kube.noon.member.domain.Member;
//import com.kube.noon.member.service.MemberService;
//import lombok.extern.log4j.Log4j2;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.test.context.junit.jupiter.SpringExtension;
//
//import java.util.List;
//
//import static com.mysema.commons.lang.Assert.assertThat;
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.junit.jupiter.api.Assertions.assertNotNull;
//
//@Log4j2
//@SpringBootTest
//public class ChatroomRepositoryTest {
//
//    @Autowired
//    private ChatroomRepository chatroomRepository;
//
//    @Autowired
//    private MemberService memberService;
//
//    @BeforeEach
//    void setUp() {
//        Chatroom chatroom = new Chatroom();
//        chatroom.setChatroomName("Test Room");
//        chatroom.setBuildingId(1);
//        chatroom.setChatroomMinTemp(18.0F);
//
//        Member member = new Member();
//        member.setMemberId("creator1");
//
//        chatroom.setChatroomCreator(member);
//        chatroom.setChatroomType(ChatroomType.GROUP_CHATTING);
//        chatroomRepository.save(chatroom);
//    }
//
//    @Test
//    public void testFindByBuildingId() {
//        List<Chatroom> result = chatroomRepository.findByBuildingId(1);
//
//        assertNotNull(result);
//        assertEquals(1, result.size());
//        assertEquals("Test Room", result.get(0).getChatroomName());
//    }
//
//    @Test
//    public void testFindByBuildingIdAndChatroomNameContaining() {
//        List<Chatroom> result = chatroomRepository.findByBuildingIdAndChatroomNameContaining(1, "Test");
//        assertNotNull(result);
//        assertEquals(1, result.size());
//        assertEquals("Test Room", result.get(0).getChatroomName());
//    }
//
//    @Test
//    public void testFindByChatroomNameContaining() {
//        List<Chatroom> result = chatroomRepository.findByChatroomNameContaining("Test");
//        assertNotNull(result);
//        assertEquals(1, result.size());
//        assertEquals("Test Room", result.get(0).getChatroomName());
//    }
//}
