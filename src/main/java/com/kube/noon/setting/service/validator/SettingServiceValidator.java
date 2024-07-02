package com.kube.noon.setting.service.validator;

import com.kube.noon.common.validator.IllegalServiceCallException;
import com.kube.noon.common.validator.Problems;
import com.kube.noon.common.validator.Validator;
import com.kube.noon.member.exception.MemberNotFoundException;
import com.kube.noon.member.service.MemberService;
import com.kube.noon.setting.dto.SettingDto;
import com.kube.noon.setting.service.SettingServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;

@Slf4j
@Validator(targetClass = SettingServiceImpl.class)
@RequiredArgsConstructor
public class SettingServiceValidator {
    private final MemberService memberService;

    public void updateSetting(String memberId, SettingDto newSetting) {
        Problems problems = new Problems();

        checkIfTheMemberExists(memberId, problems);

        Arrays.stream(newSetting.getClass().getDeclaredMethods())
                .filter((method) -> method.getName().startsWith("get"))
                .forEach((method) -> {
                    try {
                        if (method.invoke(newSetting) == null) {
                            problems.put(convertToOgnl(method.getName()), "Should be not null, but is null");
                        }
                    } catch (IllegalAccessException | InvocationTargetException e) {
                        e.printStackTrace();
                        throw new RuntimeException(e);
                    }
                });
        Problems.checkProblems(problems, getClass());
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
