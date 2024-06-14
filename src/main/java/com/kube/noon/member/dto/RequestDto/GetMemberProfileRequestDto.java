package com.kube.noon.member.dto.RequestDto;

import jakarta.persistence.Table;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString
@EqualsAndHashCode
@Setter
@Getter
public class GetMemberProfileRequestDto {

    String fromId;

    String toId;
}
