package com.kube.noon.member.dto.memberRelationship;

import com.kube.noon.member.domain.MemberRelationship;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.Page;

@Getter
@Setter
@AllArgsConstructor
public class FindMemberRelationshipListByCriteriaDto {
    private Page<MemberRelationship> memberRelationshipPage;
    private MemberRelationshipCountDto memberRelationshipCountDto;
}
