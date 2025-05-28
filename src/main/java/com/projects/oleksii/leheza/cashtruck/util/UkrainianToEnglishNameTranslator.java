package com.projects.oleksii.leheza.cashtruck.util;

import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class UkrainianToEnglishNameTranslator {
    private static final Map<String, String> TRANSLATE_RULES = new HashMap<>();

    static {
        TRANSLATE_RULES.put("А", "A");
        TRANSLATE_RULES.put("Б", "B");
        TRANSLATE_RULES.put("В", "V");
        TRANSLATE_RULES.put("Г", "H");
        TRANSLATE_RULES.put("Ґ", "G");
        TRANSLATE_RULES.put("Д", "D");
        TRANSLATE_RULES.put("Е", "E");
        TRANSLATE_RULES.put("Є", "YE");
        TRANSLATE_RULES.put("Ж", "ZH");
        TRANSLATE_RULES.put("З", "Z");
        TRANSLATE_RULES.put("И", "Y");
        TRANSLATE_RULES.put("І", "I");
        TRANSLATE_RULES.put("Ї", "YI");
        TRANSLATE_RULES.put("Й", "I");
        TRANSLATE_RULES.put("К", "K");
        TRANSLATE_RULES.put("Л", "L");
        TRANSLATE_RULES.put("М", "M");
        TRANSLATE_RULES.put("Н", "N");
        TRANSLATE_RULES.put("О", "O");
        TRANSLATE_RULES.put("П", "P");
        TRANSLATE_RULES.put("Р", "R");
        TRANSLATE_RULES.put("С", "S");
        TRANSLATE_RULES.put("Т", "T");
        TRANSLATE_RULES.put("У", "U");
        TRANSLATE_RULES.put("Ф", "F");
        TRANSLATE_RULES.put("Х", "KH");
        TRANSLATE_RULES.put("Ц", "TS");
        TRANSLATE_RULES.put("Ч", "CH");
        TRANSLATE_RULES.put("Ш", "SH");
        TRANSLATE_RULES.put("Щ", "SHCH");
        TRANSLATE_RULES.put("Ю", "YU");
        TRANSLATE_RULES.put("Я", "YA");
        TRANSLATE_RULES.put("Ь", "");
        TRANSLATE_RULES.put("’", "");
    }

    public String transliterate(String name) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < name.length(); i++) {
            char ch = name.charAt(i);
            String letter = String.valueOf(ch).toUpperCase();
            if (i > 0) {
                if (letter.equals("Є")) {
                    letter = "IE";
                } else if (letter.equals("Ї")) {
                    letter = "I";
                } else if (letter.equals("Й")) {
                    letter = "I";
                } else if (letter.equals("Ю")) {
                    letter = "IU";
                } else if (letter.equals("Я")) {
                    letter = "IA";
                } else {
                    letter = TRANSLATE_RULES.getOrDefault(letter, letter);
                }
            } else {
                letter = TRANSLATE_RULES.getOrDefault(letter, letter);
            }
            result.append(letter);
        }
        return result.toString();
    }
}
