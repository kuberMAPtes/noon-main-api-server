package com.kube.noon.member.dto;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
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
//    @Size(min = 6, max = 16, message = "회원 아이디는 6자 이상 16자 이하여야 합니다.")
    @Pattern(regexp = "^(?=.*[a-zA-Z])[a-zA-Z0-9_]{6,16}$", message = "아이디 형식이 올바르지 않습니다(6~16자). 올바른 형식 예: abc123")
    private String memberId;

    @Pattern(regexp = "^(?=.*[a-zA-Z])(?=.*[0-9])[a-zA-Z0-9!@#\\$%\\^&\\*_]{8,16}$", message = "비밀번호 형식이 올바르지 않습니다(8자~16자). 올바른 형식 예 : abc123!@,kubermap3201")
    private String pwd;

    @NotBlank(message = "닉네임이 없습니다.")
    @Pattern(regexp = "^[a-zA-Z0-9가-힣_ ]{2,20}$", message = "닉네임 형식이 올바르지 않습니다(2~20자). 올바른 형식 예: 가_힣")
    private String nickname;

    @NotBlank(message = "전화번호가 없습니다.")
    @Pattern(regexp = "^01(?:0|1|[6-9])-(?:\\d{3}|\\d{4})-\\d{4}$", message = "전화번호 형식이 올바르지 않습니다(010,011,016~019). 올바른 형식 예: 010-XXXX-XXXX")
    private String phoneNumber;


    @Nullable
    private Boolean socialSignUp;

}
