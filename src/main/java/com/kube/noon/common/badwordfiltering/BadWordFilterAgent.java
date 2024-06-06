package com.kube.noon.common.badwordfiltering;


import lombok.Getter;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;
import java.util.regex.Pattern;

@Component
public class BadWordFilterAgent extends HashSet<String> implements BadWords {
    private String substituteValue = "*";

    /**
     * 비속어 구분자
     */
    @Getter
    private final String[] badWordSeparator = {
            "!", "@", "#", "$", "%", "^", "&", "*", "(", ")", "-", "_", "=", "+",
            "`", "~", "[", "]", "{", "}", ";", ":", "\'", "\",", "\\", ".", "/", "<", ">", "?"
    };
    //대체 문자 지정
    //기본값 : *

    public BadWordFilterAgent() {
        addAll(List.of(koreaWord1));
    }

    public BadWordFilterAgent(String substituteValue) {
        addAll(List.of(koreaWord1));
        this.substituteValue = substituteValue;
    }

    //비속어 있다면 대체
    public String change(String text) {
        String[] words = stream().filter(text::contains).toArray(String[]::new);
        for (String v : words) {
            String sub = this.substituteValue.repeat(v.length());
            text = text.replace(v, sub);
        }
        return text;
    }

    /**
     *
     * @param text 필터링할 문자 ex 욕!@()설
     * @param excludedTextArray 제외될 문자열 배열 ex {"!","@","(",")"}
     * @return 욕!@()설을 욕****설로 변경
     */
    public String change(String text, String[] excludedTextArray) {
        StringBuilder singBuilder = new StringBuilder("[");
        for (String sing : excludedTextArray) singBuilder.append(Pattern.quote(sing));
        singBuilder.append("]*");
        String patternText = singBuilder.toString();

        for (String word : this) {
            if (word.length() == 1) text = text.replace(word, substituteValue);
            String[] chars = word.chars().mapToObj(Character::toString).toArray(String[]::new);
            text = Pattern.compile(String.join(patternText, chars))
                    .matcher(text)
                    .replaceAll(v -> substituteValue.repeat(v.group().length()));
        }

        return text;
    }

    //비속어가 1개라도 존재하면 true 반환
    public boolean check(String text) {
        return stream().anyMatch(text::contains);
    }

    //공백을 없는 상태 체크
    public boolean blankCheck(String text) {
        return check(text.replace(" ", ""));
    }
}