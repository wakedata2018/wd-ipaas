package com.wakedata.dw.open.gateway;

import cn.hutool.core.map.MapUtil;
import com.alibaba.fastjson.JSONObject;
import com.wakedata.dw.open.parammapping.HttpParamKind;

import java.util.Map;

/**
 * 鉴权参数上下文透传
 * @author luomeng
 * @date 2022/11/23 10:13
 */
public class OpenApiAuthParamThreadLocal {

    /**
     * 透传参数key
     */
    public static final String AUTH_PARAM_KEY = "connectorAuth";

    /**
     * 鉴权参数
     * key为参数位置 {@link HttpParamKind}
     */
    private static final ThreadLocal<Map<String, JSONObject>> AUTH_PARAM_THREAD_LOCAL = new ThreadLocal<>();
    /**
     * 在非线程池开启的子线程中传递
     */
    private static final InheritableThreadLocal<Map<String, JSONObject>> INHERITABLE_THREAD_LOCAL = new InheritableThreadLocal<>();

    /**
     * 获取鉴权参数
     * @return
     */
    public static Map<String,JSONObject> getAuthParams(){
        Map<String, JSONObject> authParams = AUTH_PARAM_THREAD_LOCAL.get();
        if(MapUtil.isEmpty(authParams)){
            authParams = INHERITABLE_THREAD_LOCAL.get();
        }
        return authParams;
    }

    /**
     * 设置鉴权参数
     * @param authParams
     */
    public static void setAuthParams(Map<String,JSONObject> authParams){
        AUTH_PARAM_THREAD_LOCAL.set(authParams);
        INHERITABLE_THREAD_LOCAL.set(authParams);
    }

    /**
     * 移除鉴权参数
     */
    public static void removeAuthParams(){
        AUTH_PARAM_THREAD_LOCAL.remove();
        INHERITABLE_THREAD_LOCAL.remove();
    }

}
