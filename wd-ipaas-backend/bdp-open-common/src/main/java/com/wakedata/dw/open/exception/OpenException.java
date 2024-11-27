package com.wakedata.dw.open.exception;

import com.wakedata.dw.open.enums.MsgCodeEnum;
import com.wakedata.dw.open.enums.OpenApiMsgCodeEnum;
import lombok.Getter;


/**
 * @author yiyufeng
 * @title OpenException
 * @projectName bdp-open
 * @date
 * @description
 */
@Getter
public class OpenException extends RuntimeException {

    private int code;

    public OpenException(String message) {
        super(message);
        this.code = MsgCodeEnum.s_error.getCode();
    }

    public OpenException(int code,String message) {
        super(message);
        this.code = code;
    }

    public OpenException(String message, Throwable cause) {
        super(message, cause);
        this.code = MsgCodeEnum.s_error.getCode();
    }

    public OpenException(int code,String message, Throwable cause) {
        super(message,cause);
        this.code = code;
    }

    public OpenException(MsgCodeEnum msgCodeEnum) {
        super(msgCodeEnum.getDesc());
        this.code = msgCodeEnum.getCode();
    }


    public OpenException(OpenApiMsgCodeEnum msgCodeEnum) {
        super(msgCodeEnum.getDesc());
        this.code = msgCodeEnum.getCode();
    }


    public OpenException(MsgCodeEnum msgCodeEnum, Throwable cause) {
        super(msgCodeEnum.getDesc(), cause);
        this.code = msgCodeEnum.getCode();
    }

    public OpenException(OpenApiMsgCodeEnum msgCodeEnum, Throwable cause) {
        super(msgCodeEnum.getDesc(), cause);
        this.code = msgCodeEnum.getCode();
    }

    @Override
    public synchronized Throwable fillInStackTrace() {
        return this;
    }

}
