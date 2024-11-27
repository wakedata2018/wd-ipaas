package com.wakedata.dw.open.parammapping;

import cn.hutool.core.util.ObjectUtil;
import com.wakedata.dw.open.constant.DwOpenConstant;

/**
 * @author ZhangXueJun
 * @title RegisterHttp
 * @date 2021/3/1 15:15
 * @projectName dw-open
 * @description 参数值位置枚举类
 */
public enum HttpParamKind {

    /**
     * QUERY参数
     */
    QUERY,
    /**
     * 请求头参数
     */
    HEAD,
    /**
     * 请求体参数
     */
    BODY,
    /**
     * FILTER参数（集成开发平台内部定义）
     */
    FILTER,
    /**
     * 公共参数
     */
    COMMON
    ;

    public static HttpParamKind convert(String code) {

        // 在Swagger中请求头对应的字符串为header
        if (ObjectUtil.equal(code, DwOpenConstant.SWAGGER_REQUEST_HEAD)) {
            return HttpParamKind.HEAD;
        }

        for (HttpParamKind httpParamKind : HttpParamKind.values()) {
            if (httpParamKind.toString().equalsIgnoreCase(code)) {
                return httpParamKind;
            }
        }
        return null;
    }

}