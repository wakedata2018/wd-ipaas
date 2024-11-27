package com.wakedata.dw.open.exception;

import com.wakedata.dw.open.enums.MsgCodeEnum;
import com.wakedata.dw.open.enums.OpenApiMsgCodeEnum;
import lombok.Getter;


/**
 * @author ZhangXueJun
 * @title ApiComponent
 * @date 2021/5/7 16:16
 * @projectName dw-open
 */
@Getter
public class OperatorOpenException extends OpenException {

    private String operatorName;

    public OperatorOpenException(String operatorName, OpenApiMsgCodeEnum msgCodeEnum, Throwable cause) {
        super(msgCodeEnum, cause);
        this.operatorName = operatorName;
    }
}
