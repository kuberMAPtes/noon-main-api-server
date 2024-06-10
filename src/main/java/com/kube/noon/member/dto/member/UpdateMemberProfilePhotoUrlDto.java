package com.kube.noon.member.dto.member;


import lombok.*;

@ToString
@EqualsAndHashCode
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateMemberProfilePhotoUrlDto {

    private String memberId;

    private String profilePhotoUrl;
}
