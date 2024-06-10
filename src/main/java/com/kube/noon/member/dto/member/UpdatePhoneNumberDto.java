package com.kube.noon.member.dto.member;

import lombok.*;

@ToString
@EqualsAndHashCode
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdatePhoneNumberDto {

    private String memberId;

    private String phoneNumber;

}
