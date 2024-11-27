package com.wakedata.dw.open.function.custom;

import cn.hutool.core.util.StrUtil;
import com.wakedata.dw.open.function.FunctionEnumSet;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 自定义函数工具类
 * @author luomeng
 * @date 2022/11/4 14:52
 */
public class CustomFunctionUtil {

    /**
     * 提取自定义函数表达式
     */
    private static final Pattern PATTERN = Pattern.compile("("+ FunctionEnumSet.TypeEnum.FUN_TYPE_CUSTOM.getMethod() +"\\.)(\\w+)(\\()");

    /**
     * 提取自定义函数名称
     * @param content
     * @return
     */
    public static Set<String> extractCustomFuncNames(String content){
        if(StrUtil.isEmpty(content)){
            return null;
        }
        Matcher matcher = PATTERN.matcher(content);
        Set<String> customFuncNames = new HashSet<>();
        while (matcher.find()) {
            customFuncNames.add(matcher.group(2));
        }
        return customFuncNames;
    }


}
