package com.kube.noon.member.dto.member;

import lombok.*;

@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class UpdatePasswordDto {

    private String memberId;

    private String pwd;

}
