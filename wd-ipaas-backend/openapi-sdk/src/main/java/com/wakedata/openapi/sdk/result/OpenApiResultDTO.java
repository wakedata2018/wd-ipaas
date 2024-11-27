package com.wakedata.openapi.sdk.result;

import lombok.Data;

import java.io.Serializable;

/**
 * 开放平台api返回格式
 * @author luomeng
 * @date 2022/8/23 19:24
 */
@Data
public class OpenApiResultDTO<T> implements Serializable {

    /**
     * 错误码
     */
    private Integer code;
    /**
     * 错误描述
     */
    private String msg;
    /**
     * 返回数据
     */
    private T data;
    /**
     * 是否成功
     */
    private Boolean success;


}
