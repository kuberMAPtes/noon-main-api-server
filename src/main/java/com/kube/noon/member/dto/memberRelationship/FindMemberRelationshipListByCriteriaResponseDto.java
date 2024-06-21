package com.kube.noon.member.dto.memberRelationship;

import com.kube.noon.member.domain.MemberRelationship;
import com.kube.noon.member.dto.memberRelationship.MemberRelationshipCountDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.Page;

@Getter
@Setter
@AllArgsConstructor
public class FindMemberRelationshipListByCriteriaResponseDto {
    private Page<MemberRelationshipDto> memberRelationshipDtoPage;
    private MemberRelationshipCountDto memberRelationshipCountDto;
}
