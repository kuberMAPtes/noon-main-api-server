package com.kube.noon.member.enums;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
public enum RelationshipType {

    FOLLOW("follow"),
    BLOCK("block");

    private final String value;



}