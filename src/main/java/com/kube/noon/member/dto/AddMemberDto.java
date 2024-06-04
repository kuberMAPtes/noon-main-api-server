package com.kube.noon.member.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@ToString
@EqualsAndHashCode
@NoArgsConstructor
@Getter
@Setter
@Builder
@AllArgsConstructor
public class AddMemberDto {

    //체크 : 비지니스로직에서 있는지 없는지 검사했었었나? 검사안해도되겠네
    @NotBlank(message = "회원 ID는 필수 입력값입니다.")
    @Size(min = 6, max = 16, message = "회원 ID는 6자 이상 16자 이하여야 합니다.")
    private String memberId;

    @Size(min = 2, max = 20, message = "닉네임은 2자 이상 20자 이하여야 합니다.")
    private String nickname;

    @Size(min = 8, max = 16, message = "비밀번호는 8자 이상 16자 이하여야 합니다.")
    private String pwd;

    @Size(min = 8, max = 20, message = "전화번호는 8자 이상 20자 이하여야 합니다.")
    private String phoneNumber;

    private Boolean socialSignUp;

}
