package com.kube.noon.setting.service.validator;

import com.kube.noon.common.validator.IllegalServiceCallException;
import com.kube.noon.common.validator.Problems;
import com.kube.noon.common.validator.Validator;
import com.kube.noon.member.exception.MemberNotFoundException;
import com.kube.noon.member.service.MemberService;
import com.kube.noon.setting.dto.SettingDto;
import com.kube.noon.setting.service.SettingServiceImpl;
import lombok.RequiredArgsConstructor;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

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
        checkProblems(problems);
    }

    private String convertToOgnl(String getter) {
        char[] charArray = getter.substring("get".length()).toCharArray();
        charArray[0] = charArray[0] < 'a' ? (char) (charArray[0] + ('a' - 'A')) : charArray[0];
        return String.valueOf(charArray);
    }

    public void findSettingOfMember(String memberId) {
        Problems problems = new Problems();
        checkIfTheMemberExists(memberId, problems);
        checkProblems(problems);
    }

    private void checkIfTheMemberExists(String memberId, Problems problems) {
        try {
            this.memberService.findMemberById(memberId);
        } catch (MemberNotFoundException e) {
            problems.put("memberId", "No such member of id=" + memberId);
        }
    }

    private void checkProblems(Problems problems) {
        if (isAnyProblem(problems)) {
            throw new IllegalServiceCallException("Problem in validation in " + this.getClass(), problems);
        }
    }

    private boolean isAnyProblem(Problems problems) {
        // "~이 아니다"라는 논리기 때문에 다소 가독성이 떨어짐
        // 그래서 따로 메소드로 빼 놓음으로써 의미를 주었다.
        return !problems.isEmpty();
    }
}
