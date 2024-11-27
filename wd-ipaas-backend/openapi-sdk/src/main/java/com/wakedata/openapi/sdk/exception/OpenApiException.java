package com.wakedata.openapi.sdk.exception;

import lombok.Getter;

/**
 * 异常信息
 * @author luomeng
 * @date 2022/8/25 14:29
 */
@Getter
public class OpenApiException extends RuntimeException {

    private Integer code;

    public OpenApiException(Integer code,String message) {
        super(message);
        this.code = code;
    }

    public OpenApiException(Integer code,String message, Throwable cause) {
        super(message, cause);
        this.code = code;
    }

    @Override
    public synchronized Throwable fillInStackTrace() {
        return this;
    }


}
