package com.kube.noon.common.badwordfiltering;



class BadWordFilteringTest {
    public static void main(String[] args) {
        BadWordFiltering filtering = new BadWordFiltering();
        System.out.println(filtering.change("안녕 ㅅ_ㅂ", new String[] {"_"}));

        String[] badWordSeparator = {
                "!", "@", "#", "$", "%", "^", "&", "*", "(", ")", "-", "_", "=", "+",
                "`", "~", "[", "]", "{", "}", ";", ":", "\'", "\",", "\\", ".", "/", "<", ">", "?"
        };

        BadWordFiltering filtering1 = new BadWordFiltering();
        filtering1.check("안녕 ㅅ_ㅂ");
        filtering1.blankCheck("안녕 ㅅ_ㅂ");
        System.out.println((filtering1.check("")));
        System.out.println("검증중");
        System.out.println(filtering.change("fuck",badWordSeparator).contains("*"));

    }
}
