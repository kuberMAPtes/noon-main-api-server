package com.kube.noon.member.dto;

import com.kube.noon.member.enums.RelationshipType;
import lombok.*;


@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class MemberRelationshipDto {

    private String fromId;

    private String toId;

    private RelationshipType relationshipType;

    private Boolean activated;

}