package com.kube.noon.chat.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@AllArgsConstructor
@Getter
@ToString
public class LiveliestChatroomDto {
    private final String chatroomName;
    private final String liveliness; // TODO: Change to domain enum
}
