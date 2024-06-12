package com.kube.noon.member.controller;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ApiResponse<T> {
    private String message;
    private T info;
    //requestId는 Header에 담아서 보내줘야한다.
}
