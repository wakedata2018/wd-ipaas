package com.wakedata.dw.open.dto;

import com.wakedata.dw.open.enums.MsgCodeEnum;

import java.io.Serializable;

/**
 * @author pengxy
 * @title ResultDTO
 * @projectName bdp-open
 * @date 2016/10/31
 */
public class ResultDTO<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    private Boolean success = true;

    private T data;

    private Integer errorCode;

    private String errorMessage;


    public boolean isSuccess() {
        return success;
    }


    public void setSuccess(boolean success) {
        this.success = success;
    }


    public T getData() {
        return data;
    }


    public void setData(T data) {
        this.data = data;
    }


    public Integer getErrorCode() {
        return errorCode;
    }


    public void setErrorCode(Integer errorCode) {
        this.errorCode = errorCode;
    }


    public String getErrorMessage() {
        return errorMessage;
    }


    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public void setFailed(Integer errorCode, String errorMessage) {
        this.success = false;
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }

    public void setFailed(MsgCodeEnum msgCodeEnum, String errorMessage) {
        this.success = false;
        this.errorCode = msgCodeEnum.getCode();
        this.errorMessage = errorMessage;
    }

    public void setFailed(MsgCodeEnum msgCodeEnum) {
        this.success = false;
        this.errorCode = msgCodeEnum.getCode();
        this.errorMessage = msgCodeEnum.getDesc();
    }

    public void setMessage(MsgCodeEnum message, boolean isSuccess) {
        this.success = isSuccess;
        this.errorCode = message.getCode();
        this.errorMessage = message.getDesc();
    }

    public ResultDTO() {
        this.success = true;
        this.errorCode = MsgCodeEnum.s_success.getCode();
        this.errorMessage = MsgCodeEnum.s_success.getDesc();
        this.data = null;
    }

    public static <T> ResultDTO<T> success(T data) {
        ResultDTO<T> resultDTO = new ResultDTO<>();
        resultDTO.setData(data);
        return resultDTO;
    }

}
