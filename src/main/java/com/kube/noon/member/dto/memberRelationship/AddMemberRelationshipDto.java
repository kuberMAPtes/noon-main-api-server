package com.kube.noon.member.dto.memberRelationship;

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

    private String fromId;

    private String toId;

    private RelationshipType relationshipType;

    private Boolean activated;

}