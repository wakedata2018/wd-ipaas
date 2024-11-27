package com.wakedata.dw.open.function.custom;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONUtil;
import com.wakedata.common.core.GlobalApplicationContext;
import com.wakedata.dw.open.config.DwOpenCommonConfig;
import com.wakedata.dw.open.exception.OpenException;
import com.wakedata.dw.open.function.FunctionEnumSet;
import groovy.lang.GroovyClassLoader;
import groovy.lang.GroovyObject;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;

import static com.wakedata.dw.open.enums.OpenApiMsgCodeEnum.w_cloud_function_execute_fail;

/**
 * groovy 脚本执行器
 *
 * @author luomeng
 * @date 2022/10/31 15:00
 */
@Slf4j
public class GroovyScriptEngine {

    /**
     * groovy脚本加载器
     */
    private static final GroovyClassLoader GROOVY_CLASS_LOADER = new GroovyClassLoader(GroovyScriptEngine.class.getClassLoader());


    /**
     * 获取自定义函数实例对象
     * @param function
     * @return
     */
    public static Object getFuncInstance(String function) {
        String functionName = function.replace(FunctionEnumSet.TypeEnum.FUN_TYPE_CUSTOM.getMethod() + ".","");
        CustomFunction customFunction = GlobalApplicationContext.getBean(CustomFunctionHelper.class).getCustomFunctionByName(functionName,GlobalApplicationContext.getBean(DwOpenCommonConfig.class).getPlatformLesseeId());
        if(ObjectUtil.isNull(customFunction)){
            return null;
        }
        return getGroovyIntstance(functionName, customFunction.getCode());
    }

    /**
     * groovy脚本实例化为java对象
     * @param name
     * @param code
     * @return
     */
    private static GroovyObject getGroovyIntstance(String name,String code){

        Class groovyClass = GROOVY_CLASS_LOADER.parseClass((Objects.requireNonNull(code)));
        try {
            return (GroovyObject) groovyClass.newInstance();
        } catch (Exception e) {
            log.error("自定义函数实例化出错：functionName:{},functionCode:{}", name,code, e);
            throw new OpenException(w_cloud_function_execute_fail.getCode(), String.format(w_cloud_function_execute_fail.getDesc(), name));
        }
    }


    /**
     * 执行脚本
     *
     * @param customFunction 自定义函数
     * @return
     */
    public static Object execute(CustomFunction customFunction) {
        try {
            GroovyObject groovyObject = getGroovyIntstance(customFunction.getName(), customFunction.getCode());
            Object[] params = CollUtil.isEmpty(customFunction.getParamList()) ? null : customFunction.getParamList().stream().map(CustomFunction.FunctionParam::getValue).toArray();
            Object result = groovyObject.invokeMethod(customFunction.getName(), params);
            return convert(result, customFunction.getReturnType());
        } catch (Exception e) {
            log.error("自定义函数执行出错：function:{}", customFunction, e);
            throw new OpenException(w_cloud_function_execute_fail.getCode()
                    , String.format(w_cloud_function_execute_fail.getDesc(), customFunction.getName(), e.getMessage()));
        }
    }

    /**
     * 类型转换
     * @param result
     * @param returnType
     * @return
     */
    private static Object convert(Object result,String returnType){
        if(ObjectUtil.isNull(result)){
            return null;
        }
        CustomFunctionReturnTypeEnum returnTypeEnum = CustomFunctionReturnTypeEnum.getDescriptionReturnTypeEnum(returnType);
        if(ObjectUtil.isNull(returnTypeEnum)){
            return null;
        }
        switch (returnTypeEnum){

            case JSON_OBJECT:
                result = Convert.convert(com.alibaba.fastjson.JSONObject.class,result);
                break;
            case LIST_OBJECT:
                result = Convert.toList(result);
                break;
            case MAP_OBJECT:
                result = Convert.toMap(String.class, Object.class,result);
                break;
            case INTEGER:
                result = Convert.toInt(result);
                break;
            case LONG:
                result = Convert.toLong(result);
                break;
            case BOOLEAN:
                result = Convert.toBool(result);
                break;
            case DOUBLE:
                result = Convert.toDouble(result);
                break;
            case STRING:
                result = Convert.toStr(result);
                break;
            case DATE:
                result = Convert.toDate(result);
                break;
            default:
                break;
        }

        return result;
    }


}
