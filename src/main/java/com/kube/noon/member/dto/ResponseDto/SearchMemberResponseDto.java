package com.kube.noon.member.dto.ResponseDto;

import com.kube.noon.member.domain.Member;
import lombok.*;
import org.springframework.beans.BeanUtils;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder(access = AccessLevel.PRIVATE)
@Getter
@ToString
public class SearchMemberResponseDto {
    private String memberId;
    private String nickname;
    private String phoneNumber;
    private String profilePhotoUrl;
    private String profileIntro;
    private boolean follower;
    private boolean following;

    public static SearchMemberResponseDto of(Member member, boolean follower, boolean following) {
        return SearchMemberResponseDto.builder()
                .memberId(member.getMemberId())
                .nickname(member.getNickname())
                .phoneNumber(member.getPhoneNumber())
                .profilePhotoUrl(member.getProfilePhotoUrl())
                .profileIntro(member.getProfileIntro())
                .follower(follower)
                .following(following)
                .build();
    }
}
