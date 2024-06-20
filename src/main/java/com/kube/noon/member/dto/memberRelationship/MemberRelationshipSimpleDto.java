package com.kube.noon.member.dto.memberRelationship;


import com.kube.noon.member.domain.Member;
import com.kube.noon.member.dto.member.MemberSimpleDto;
import com.kube.noon.member.enums.RelationshipType;
import lombok.*;

@ToString
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@EqualsAndHashCode
public class MemberRelationshipSimpleDto {

    private int memberRelationshipId;

    private RelationshipType relationshipType;

    private Boolean activated;

    private MemberSimpleDto fromMember;

    private MemberSimpleDto toMember;

}
