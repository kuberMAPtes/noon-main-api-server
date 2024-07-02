package com.kube.noon.setting.service.validator;

import com.kube.noon.common.validator.Problems;
import com.kube.noon.common.validator.Validator;
import com.kube.noon.member.service.MemberService;
import com.kube.noon.setting.dto.SettingDto;
import com.kube.noon.setting.service.SettingServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.stream.Collectors;

@Slf4j
@Validator(targetClass = SettingServiceImpl.class)
@RequiredArgsConstructor
public class SettingServiceValidator {
    private final MemberService memberService;

    public void updateSetting(String memberId, SettingDto newSetting) {
        Problems problems = validateNull(newSetting);
        checkIfTheMemberExists(memberId, problems);
        Problems.checkProblems(problems, getClass());
    }

    private Problems validateNull(SettingDto toCheck) {
        return new Problems(Arrays.stream(toCheck.getClass().getDeclaredMethods())
                .filter((method) -> method.getName().startsWith("get"))
                .filter((method) -> {
                    try {
                        return method.invoke(toCheck) == null;
                    } catch (IllegalAccessException | InvocationTargetException e) {
                        e.printStackTrace();
                        throw new RuntimeException(e);
                    }
                })
                .collect(
                        Collectors.toMap(
                                (m) -> convertToOgnl(m.getName()),
                                (m) -> "Should be not null, but is null",
                                (v1, v2) -> v2
                        )
                )
        );
    }

    private String convertToOgnl(String getter) {
        char[] charArray = getter.substring("get".length()).toCharArray();
        charArray[0] = charArray[0] < 'a' ? (char) (charArray[0] + ('a' - 'A')) : charArray[0];
        return String.valueOf(charArray);
    }

    public void findSettingOfMember(String memberId) {
        Problems problems = new Problems();
        checkIfTheMemberExists(memberId, problems);
        Problems.checkProblems(problems, getClass());
    }

    private void checkIfTheMemberExists(String memberId, Problems problems) {
        if (this.memberService.findMemberById(memberId).isEmpty()) {
            problems.put("memberId", "No such member of id=" + memberId);
        }
    }
}
