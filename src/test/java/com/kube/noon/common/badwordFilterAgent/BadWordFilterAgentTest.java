package com.kube.noon.common.badwordFilterAgent;


import com.kube.noon.common.badwordfiltering.BadWordFilterAgent;

class BadWordFilterAgentTest {
    public static void main(String[] args) {
        BadWordFilterAgent filtering = new BadWordFilterAgent("*");
        System.out.println(filtering.change("안녕 ㅅ_ㅂ", new String[] {"_"}));

        String[] badWordSeparator = {
                "!", "@", "#", "$", "%", "^", "&", "*", "(", ")", "-", "_", "=", "+",
                "`", "~", "[", "]", "{", "}", ";", ":", "\'", "\",", "\\", ".", "/", "<", ">", "?"
        };

        BadWordFilterAgent filtering1 = new BadWordFilterAgent("*");
        filtering1.check("안녕 ㅅ_ㅂ");
        filtering1.blankCheck("안녕 ㅅ_ㅂ");
        System.out.println((filtering1.check("")));
        System.out.println("검증중");

        String normalWord = "안!(@#$(!&#$*!(&$#녕";
        String cleanWord = "*****!*@#&!@#&(***";
        String badWord = "씨@!@#****발";
        System.out.println(filtering.change(normalWord.replace("*",""),badWordSeparator).contains("*"));

    }
}
