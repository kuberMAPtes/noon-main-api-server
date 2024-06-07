package com.kube.noon.member.dto;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
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
    @NotBlank(message = "회원 아이디가 없습니다.")
    @Size(min = 6, max = 16, message = "회원 아이디는 6자 이상 16자 이하여야 합니다.")
    private String memberId;

    private String pwd;

    @NotBlank(message = "닉네임이 없습니다.")
    @Size(min = 2, max = 20, message = "닉네임은 2자 이상 20자 이하여야 합니다.")
    private String nickname;

    @NotBlank(message = "전화번호가 없습니다.")
    @Pattern(regexp = "^01(?:0|1|[6-9])-(?:\\d{3}|\\d{4})-\\d{4}$", message = "전화번호 형식이 올바르지 않습니다. 올바른 형식 예: 010-XXXX-XXXX")
    private String phoneNumber;


    @Nullable
    private Boolean socialSignUp;

}
