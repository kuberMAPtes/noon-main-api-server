package com.kube.noon.member.dto;


import lombok.*;

@ToString
@EqualsAndHashCode
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateMemberProfilePhotoUrlDto {

    private String memberId;

    private String profilePhotoUrl;
}
