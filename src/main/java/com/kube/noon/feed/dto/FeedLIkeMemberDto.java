package com.kube.noon.feed.dto;

import com.kube.noon.feed.domain.Feed;
import com.kube.noon.member.domain.Member;
import lombok.*;

import java.util.List;
import java.util.stream.Collectors;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class FeedLIkeMemberDto {
    private String memberId;
    private String memberNickname;

    public static FeedLIkeMemberDto toDto(Member member) {
        return FeedLIkeMemberDto.builder()
                .memberId(member.getMemberId())
                .memberNickname(member.getNickname())
                .build();
    }

    public static List<FeedLIkeMemberDto> toDtoList(List<Member> memberList) {
        return memberList.stream().map(FeedLIkeMemberDto::toDto).collect(Collectors.toList());
    }
}
