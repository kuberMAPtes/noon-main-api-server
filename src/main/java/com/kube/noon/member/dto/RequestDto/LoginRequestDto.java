package com.kube.noon.member.dto.RequestDto;

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
