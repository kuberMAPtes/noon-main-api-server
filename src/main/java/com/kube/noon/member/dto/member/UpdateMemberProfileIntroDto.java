package com.kube.noon.member.dto.member;

import lombok.*;

@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class UpdateMemberProfileIntroDto {

    String memberId;

    String profileIntro;

}
