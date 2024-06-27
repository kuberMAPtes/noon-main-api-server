package com.kube.noon.member.dto.auth;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@ToString
@EqualsAndHashCode
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class googleLoginRequestDto {

    @JsonProperty("memberId")
    String memberId;

    @JsonProperty("authorizeCode")
    String accessToken;

    @JsonProperty("refreshToken")
    String refreshToken;

    @JsonProperty("nickname")
    String nickname;

    @JsonProperty("profilePhotoUrl")
    String profilePhotoUrl;

    @JsonProperty("phoneNumber")
    String phoneNumber;
}
