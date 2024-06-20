package com.kube.noon.chat.repository;

import com.kube.noon.building.domain.Building;
import com.kube.noon.building.repository.BuildingProfileRepository;
import com.kube.noon.chat.domain.Chatroom;
import com.kube.noon.chat.domain.ChatroomType;
import com.kube.noon.common.constant.PagingConstants;
import com.kube.noon.member.domain.Member;
import com.kube.noon.member.repository.MemberRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class TestChatroomRepository {
    @Autowired
    ChatroomRepository chatroomRepository;

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    BuildingProfileRepository buildingProfileRepository;

    @Test
    void findByChatroomNameContaining_paging() {
        Building sampleBuilding = Building.builder()
                .buildingName("p-sample-building-1")
                .profileActivated(true)
                .roadAddr("sample-addr")
                .latitude(37.51241313415)
                .longitude(126.15182451)
                .feedAiSummary("sample summary")
                .build();
        this.buildingProfileRepository.save(sampleBuilding);

        Member sampleCreator = Member.builder()
                .memberId("sampleMember")
                .nickname("samplenic")
                .pwd("samplepwd")
                .phoneNumber("01051582413")
                .build();
        this.memberRepository.addMember(sampleCreator);

        addSampleChatroom(sampleCreator.getMemberId(),
                sampleBuilding.getBuildingId(),
                "p-sample-chatroom123",
                ChatroomType.GROUP_CHATTING,
                0.0F);

        Page<Chatroom> result1 =
                this.chatroomRepository
                        .findByChatroomNameContaining("p-sample-chatroom", PageRequest.of(0, PagingConstants.PAGE_SIZE));

        assertThat(result1.getTotalElements()).isEqualTo(1);
        assertThat(result1.getTotalPages()).isEqualTo(1);
        assertThat(result1.getContent().get(0).getChatroomName()).isEqualTo("p-sample-chatroom123");

        Page<Chatroom> result2 =
                this.chatroomRepository
                        .findByChatroomNameContaining("p-sample-chatroom", PageRequest.of(1, PagingConstants.PAGE_SIZE));

        assertThat(result2.getTotalElements()).isEqualTo(1);
        assertThat(result2.getTotalPages()).isEqualTo(1);
        assertThat(result2.getContent()).isEmpty();
    }

    private void addSampleChatroom(String chatroomCreatorId,
                                   int buildingId,
                                   String chatroomName,
                                   ChatroomType chatroomType,
                                   Float chatroomMinTemp) {
        Chatroom chatroom = new Chatroom();
        chatroom.setChatroomCreatorId(chatroomCreatorId);
        chatroom.setBuilding(this.buildingProfileRepository.findBuildingProfileByBuildingId(buildingId));
        chatroom.setChatroomName(chatroomName);
        chatroom.setChatroomType(chatroomType);
        chatroom.setChatroomMinTemp(chatroomMinTemp);
        chatroom.setActivated(true);
        this.chatroomRepository.save(chatroom);
    }
}