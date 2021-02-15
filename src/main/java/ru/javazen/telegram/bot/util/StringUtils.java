package ru.javazen.telegram.bot.util;

import lombok.experimental.UtilityClass;

@UtilityClass
public class StringUtils {
    public String cutFirst(String str) {
        return str.substring(1);
    }

    public String cutLast(String str){
        return str.substring(0, str.length() - 1);
    }

    public String cutFirstLast(String str){
        return str.substring(1, str.length() - 1);
    }
}
