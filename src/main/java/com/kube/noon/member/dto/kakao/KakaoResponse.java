package com.kube.noon.member.dto.kakao;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode
public class KakaoResponse {

    private String id;
    private KakaoAccount kakao_account;

}
