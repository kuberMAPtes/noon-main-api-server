package com.kube.noon.member.dto;


import com.kube.noon.member.enums.MemberSearchCondition;
import com.kube.noon.member.enums.RelationshipType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

//==>리스트화면을 모델링(추상화/캡슐화)한 Bean
@Getter
@Setter
@NoArgsConstructor
@ToString
public class SearchDto<T> {

    ///Field
    private MemberSearchCondition memberSearchCondition;
    private RelationshipType memberRelationshipSearchCondition;
    private T searchKeyword;
    private LocalDateTime startTime;
    private LocalDateTime endTime;

}