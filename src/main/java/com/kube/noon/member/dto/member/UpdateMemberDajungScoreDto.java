package com.kube.noon.member.dto.member;


import lombok.*;

@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class UpdateMemberDajungScoreDto {

    private String memberId;

    private int dajungScore;


}
