package com.wakedata.dw.open.utils;

import cn.hutool.core.util.ObjectUtil;

import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

/**
 * @author luomeng
 * @Description 获取请求参数
 * @createTime 2022-08-07 18:29:00
 */
public class HttpParamUtil {

    /**
     * 获取请求参数
     * @param request
     * @return
     */
    public static Map<String,String> getRequestParams(HttpServletRequest request) {
        Map<String,String> map = new HashMap<>();
        Enumeration paramNames = request.getParameterNames();
        while (paramNames.hasMoreElements()) {
            String paramName = (String) paramNames.nextElement();

            String[] paramValues = request.getParameterValues(paramName);
            if (paramValues.length > 0) {
                String paramValue = paramValues[0];
                if (ObjectUtil.isNotEmpty(paramValue)) {
                    map.put(paramName, paramValue);
                }
            }
        }

        return map;
    }

}
