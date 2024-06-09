package com.kube.noon.member.dto;

import com.kube.noon.member.domain.Member;
import com.kube.noon.member.enums.RelationshipType;
import lombok.*;

@ToString
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@EqualsAndHashCode
public class MemberRelationshipDto {

    private int memberRelationshipId;

    private RelationshipType relationshipType;

    private Boolean activated;

    private Member fromMember;

    private Member toMember;

}
