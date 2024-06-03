package com.kube.noon.member.dto;

import lombok.*;

@ToString
@EqualsAndHashCode
@NoArgsConstructor
@Getter
@Setter
@Builder
@AllArgsConstructor
public class AddMemberDto {

    private String memberId;

    private String nickname;

    private String pwd;

    private String phoneNumber;

    private boolean socialSignUp;

}
