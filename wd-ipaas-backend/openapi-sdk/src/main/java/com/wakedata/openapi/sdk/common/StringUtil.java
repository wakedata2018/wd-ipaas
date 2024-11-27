package com.wakedata.openapi.sdk.common;

import cn.hutool.core.util.StrUtil;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 字符串操作
 * @author luomeng
 * @date 2022/10/29 11:36
 */
public class StringUtil {

    /**
     * 字符替换,并大写
     */
    private static final Pattern PATTERN_REPLACE_UPPERCASE = Pattern.compile("(\\/|\\.|_|-)([a-zA-Z0-9])");
    /**
     * 字符替换
     */
    private static final Pattern PATTERN_REPLACE = Pattern.compile("(\\/|\\.|_|-)");
    /**
     * 首字母为数字
     */
    private static final Pattern PATTERN_START_WITH_NUMBER = Pattern.compile("[0-9].*");
    /**
     * 首字母大写
     */
    private static final Pattern PATTERN_CAPITALIZE = Pattern.compile("^.");


    /**
     * 字符转换
     * @param input
     * @param isUpperCase
     * @param replacement
     * @return
     */
    public static String replace(String input,boolean isUpperCase,String replacement){
        if(StrUtil.isEmpty(input)){
            return input;
        }
        Matcher matcher;
        if(isUpperCase){
            matcher = PATTERN_REPLACE_UPPERCASE.matcher(input);
            StringBuffer sb = new StringBuffer();
            while (matcher.find()){
                matcher.appendReplacement(sb,matcher.group(matcher.groupCount()).toUpperCase());
            }
            matcher.appendTail(sb);
            input = sb.toString();
        }
        matcher = PATTERN_REPLACE.matcher(input);
        StringBuffer sb = new StringBuffer();
        while (matcher.find()){
            matcher.appendReplacement(sb, replacement);
        }
        matcher.appendTail(sb);
        input = sb.toString();
        matcher = PATTERN_START_WITH_NUMBER.matcher(input);
        if(matcher.find()){
            input = "A" + input;
        }
        return input;
    }


    /**
     * 字符串截取
     * @param input
     * @param character
     * @return
     */
    public static String substring(String input,String character){
        if(StrUtil.isEmpty(input)){
            return input;
        }
        int indexOf = input.indexOf(character);
        String res = "";
        if(indexOf != -1){
            res = input.substring(indexOf + 1);
        }
        if(StrUtil.isEmpty(res)){
            return input;
        }
        return res;
    }



    /**
     * 首字母大写
     * @param input
     * @return
     */
    public static String capitalize(String input){
        Matcher matcher = PATTERN_CAPITALIZE.matcher(input);
        if(matcher.find()){
            return matcher.replaceFirst(matcher.group().toUpperCase());
        }
        return input;
    }
}
