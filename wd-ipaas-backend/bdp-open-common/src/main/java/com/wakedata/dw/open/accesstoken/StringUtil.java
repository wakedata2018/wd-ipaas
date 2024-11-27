package com.wakedata.dw.open.accesstoken;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author pengxu
 * @Date 2018/12/4.
 */
public class StringUtil {

    public static boolean isEmpty(String s){
        return null == s || s.trim().length() == 0;
    }

    public static boolean areEmpty(String key, String value){
        return null == key || key.trim().length() == 0 ||
                null == value || value.trim().length() == 0;
    }

    public static String removeEmpty(String str){
        return str == null ? null:str.replaceAll("\\s*", "");
    }


    public static List<Long> str2List(String str) {
        if (StringUtils.isBlank(str)) {
            return null;
        }
        String[] split = str.split(",");
        List<Long> ids = new ArrayList<>();
        for (String s : split) {
            ids.add(Long.valueOf(s));
        }
        return ids;
    }

//    /**
//     * 字符串等长切割
//     * @param str 传入的字符串
//     * @param length 切割的最长单位
//     * @return
//     */
//    public static List<String> subStrByLength(String str, int length) {
//        if (StringUtils.isBlank(str) || length < 1) {
//            return null;
//        }
//        int listLength = (str.length() + length - 1)/length;
//        List<String> result = new ArrayList<>(listLength);
//        for (int i = 1; i <= listLength; i ++) {
//            String temp = str.substring(length * (i - 1), length * i > str.length() ? str.length() : length * i);
//            result.add(temp);
//        }
//        return result;
//    }
}
