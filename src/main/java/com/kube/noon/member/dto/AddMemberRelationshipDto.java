package com.kube.noon.member.dto;

import com.kube.noon.member.domain.Member;
import com.kube.noon.member.enums.RelationshipType;
import lombok.*;


@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class AddMemberRelationshipDto {

    private int memberRelationshipId;

//    private String fromId;

//    private String toId;

    private RelationshipType relationshipType;

    private boolean activated = true;

    private Member fromMember;

    private Member toMember;

}
