package com.kube.noon.member.dto;

import lombok.*;

@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class UpdatePasswordDto {

    private String memberId;

    private String pwd;

}
