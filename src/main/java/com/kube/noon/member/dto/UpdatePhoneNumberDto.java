package com.kube.noon.member.dto;

import lombok.*;

@ToString
@EqualsAndHashCode
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UpdatePhoneNumberDto {

    private String memberId;

    private String PhoneNumber;

}