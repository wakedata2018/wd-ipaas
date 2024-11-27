package com.wakedata.dw.open.function;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.wakedata.common.core.GlobalApplicationContext;
import com.wakedata.dw.open.config.DwOpenCommonConfig;
import com.wakedata.dw.open.function.custom.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 枚举转换
 *
 * @author luomeng
 * @date 2022/8/18 19:38
 */
public class FunctionEnumUtil {

    /**
     * 获取当前支持的函数列表
     *
     * @return
     */
    public static List<FunctionVo> getSupportFunctionList() {
        FunctionEnumSet.TypeEnum[] values = FunctionEnumSet.TypeEnum.values();
        return Arrays.stream(values)
                .map(x -> new FunctionVo(x.getMethod(), x.getDescription(), parseEnumList(x.getMethod(), x.getFunctionEnum())))
                .filter(x->CollUtil.isNotEmpty(x.getList()))
                .collect(Collectors.toList());
    }


    /**
     * 解析枚举为对象
     *
     * @param method
     * @param enumValues
     * @return
     */
    private static List<FunctionEnumSet.FunctionEnumVo> parseEnumList(String method, FunctionEnumSet[] enumValues) {
        //自定义函数
        if (FunctionEnumSet.TypeEnum.FUN_TYPE_CUSTOM.getMethod().equals(method)) {
            return getCloudFunctionList();
        }
        //内置函数
        return Arrays.stream(enumValues)
                .map(x -> new FunctionEnumSet.FunctionEnumVo(x.method(), x.returnType(), x.param(), x.paramDesc(), x.description(), x.example()))
                .collect(Collectors.toList());
    }

    /**
     * 获取自定义函数
     *
     * @return
     */
    private static List<FunctionEnumSet.FunctionEnumVo> getCloudFunctionList() {
        List<CustomFunction> customFunctionList = GlobalApplicationContext.getBean(CustomFunctionHelper.class).getPublishCustomFunctionList(GlobalApplicationContext.getBean(DwOpenCommonConfig.class).getPlatformLesseeId());
        if (CollUtil.isEmpty(customFunctionList)) {
            return new ArrayList<>();
        }
        return customFunctionList.stream().map(x -> {
            String param = "";
            String paramDesc = "";
            if (CollUtil.isNotEmpty(x.getParamList())) {
                for (CustomFunction.FunctionParam functionParam : x.getParamList()) {
                    param += functionParam.getType() + " " + functionParam.getName() + ",";
                    paramDesc += functionParam.getName() + ":" + functionParam.getDesc() + "\n";
                }
            }
            if (StrUtil.isNotEmpty(param)) {
                param = param.substring(0, param.length() - 1);
            }
            param = "(" + param + ")";
            String apiName = FunctionEnumSet.TypeEnum.FUN_TYPE_CUSTOM.getMethod() + "." + x.getName();
            return new FunctionEnumSet.FunctionEnumVo(apiName, x.getReturnType(), param, paramDesc, x.getDesc(), null);
        }).collect(Collectors.toList());
    }


    /**
     * 获取自定义函数支持列表
     * @return
     */
    public static CustomFunctionSupportVo getCustomFunctionSupportList() {
        CustomFunctionSupportVo customFunctionSupportVo = new CustomFunctionSupportVo();
        customFunctionSupportVo.setSupportTypeList(Arrays.stream(CustomFunctionReturnTypeEnum.values())
                .map(x->new CustomFunctionSupportVo.SupportType(x.getType(),x.getDesc()))
                .collect(Collectors.toList()));
        customFunctionSupportVo.setSupportLanguageList(Arrays.stream(CustomFunctionTemplateEnum.values())
                .map(x->new CustomFunctionSupportVo.SupportLanguage(x.getLanguage(),x.getDesc(),x.getCode()))
                .collect(Collectors.toList()));
        return customFunctionSupportVo;

    }
}
