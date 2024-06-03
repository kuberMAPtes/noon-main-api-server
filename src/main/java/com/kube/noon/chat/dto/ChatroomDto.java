package com.kube.noon.chat.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class ChatroomDto {

    private int chatroomID;
    private Float chatroomMinTemp;
    private String chatroomName;
    private String chatroomCreatorId;
    private String chatroomType;
}
