package com.umanji.umanjiapp.helper;

/**
 * Created by paul on 6/12/16.
 */
public class Test {
    public static void main(String[] args) {
        String inputStr = "com.app.umnaji.aaa/climb";

        int howlong = inputStr.length();

        char aaa;
        int startStr = 0;

        startLoop:
        for (int idx = 0;  idx < howlong; idx++) {

            aaa = inputStr.charAt(idx);

            if(aaa == '/'){
                    startStr = idx+1;
                    break startLoop;
                }
            }

        String answer = inputStr.substring(startStr, howlong);
        System.out.println("answer : " +answer);

        }



    }



/*
*
* txt1.length();
* txt1.contains("나다")
* txt2.indexOf("테스트")
* txt2.matches(".*테스트.*")
* txt3.matches(".*[0-9].*")
*
*
* String txt1 = "중국집=쭝국집, 짜장면, 짬뽕 /" +
                "역사=한국사, 중국역사, 중국사, 일본역사, 고대사, 상고사 /" +
                "커피=커피샵, 커피전문점, 스타벅스, 탐앤탐스";

        int position =0;
        int wordStart = 0;
        int wordEnd = 0;
        String myWord = "";

        String inputWord = "짬뽕";

        char aaa;

        if (txt1.contains(inputWord)) {
            position = txt1.indexOf(inputWord);          // 입력한 단어의 위치
            System.out.println("position : " + position);



            startLoop:
            for (int idx = position; idx >= 0; idx--) {


                aaa = txt1.charAt(idx);

                if(aaa == '/'){
                    wordStart= idx +1;       // 찾는 단어의 시작 위치
                    if(wordStart != 0){
                        System.out.println("start : "+ wordStart);
                        break startLoop;
                    }
                }

            }




                    wordEnd= txt1.indexOf("=", wordStart);       // 찾는 단어의 마지막 위치
                        System.out.println("end : "+ wordEnd);




            myWord = txt1.substring(wordStart, wordEnd); // 찾는 단어
            System.out.println("answer : "+ myWord);
        } else {
            System.out.println("입력한 단어");
        }
*
* */
