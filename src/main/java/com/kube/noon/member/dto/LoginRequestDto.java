package com.kube.noon.member.dto;

import lombok.*;

@ToString
@EqualsAndHashCode
@NoArgsConstructor
@Getter
@Setter
@Builder
@AllArgsConstructor
public class LoginRequestDto {

    String memberId;

    String pwd;

}
