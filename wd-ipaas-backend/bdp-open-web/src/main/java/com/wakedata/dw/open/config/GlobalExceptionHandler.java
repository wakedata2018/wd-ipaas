package com.wakedata.dw.open.config;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONUtil;
import com.wakedata.common.core.dto.ResultDTO;
import com.wakedata.dw.common.exception.DwException;
import com.wakedata.dw.open.enums.MsgCodeEnum;
import com.wakedata.dw.open.exception.OpenException;
import com.wakedata.dw.open.service.utils.RequestParamUtils;
import com.wakedata.dw.open.service.utils.DocumentManagementUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindException;
import org.springframework.validation.ObjectError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.io.IOException;
import java.util.List;

/**
 * @author yiyufeng tanzhi
 * @title GlobalExceptionHandler
 * @projectName bdp-open
 * @date
 * @description
 */
@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler
    @ResponseBody
    public ResultDTO exceptionHandler(Exception ex, HttpServletResponse response, HttpServletRequest request) {
        response.setCharacterEncoding("UTF-8");
        int code = MsgCodeEnum.s_error.getCode();
        String message = MsgCodeEnum.s_error.getDesc();
        if (ex instanceof MissingServletRequestParameterException) {
            code = MsgCodeEnum.w_empty_argument.getCode();
            MissingServletRequestParameterException bindEx = (MissingServletRequestParameterException) ex;
            message = MsgCodeEnum.w_empty_argument.getDesc() + bindEx.getParameterName();
        }
        if (ex instanceof BindException) {
            code = MsgCodeEnum.w_illegal_argument.getCode();
            BindException bindEx = (BindException) ex;
            List<ObjectError> ers = bindEx.getAllErrors();
            if (ers != null && ers.size() > 0) {
                message = ers.get(0).getDefaultMessage();
            }
        }
        if (ex instanceof MethodArgumentNotValidException) {
            code = MsgCodeEnum.w_illegal_argument.getCode();
            MethodArgumentNotValidException bindEx = (MethodArgumentNotValidException) ex;
            List<ObjectError> ers = bindEx.getBindingResult().getAllErrors();
            if (ers != null && ers.size() > 0) {
                message = ers.get(0).getDefaultMessage();
            }
        }
        if (ex instanceof HttpRequestMethodNotSupportedException) {
            code = MsgCodeEnum.w_operation_illegal_http_method.getCode();
            BindException bindEx = (BindException) ex;
            List<ObjectError> ers = bindEx.getAllErrors();
            if (ers != null && ers.size() > 0) {
                message = ers.get(0).getDefaultMessage();
            }
        }
        if (ex instanceof ConstraintViolationException) {
            code = MsgCodeEnum.w_illegal_argument.getCode();
            ConstraintViolationException bindEx = (ConstraintViolationException) ex;
            for (ConstraintViolation<?> s : bindEx.getConstraintViolations()) {
                message = s.getPropertyPath() + s.getMessage() + " ";
            }
        }
        if (ex instanceof OpenException) {
            message = ex.getMessage();
            if (message.endsWith("\r\n")) {
                message = message.trim();
            }
            code = ((OpenException) ex).getCode();
        }
        if (ex instanceof DwException) {
            DwException e = (DwException) ex;
            code = e.getCode();
            message = e.getMessage();
            Object[] param = e.getParams();
            if (null != param) {
                message = String.format(message, param);
            }
        }
        ResultDTO r = new ResultDTO();
        r.setCode(code);
        r.setMsg(message);
        r.setSuccess(Boolean.FALSE);
        log.error("发生了异常", ex);
        //鉴别响应体中的返回格式是否是xml格式，是则进行转换
        if (ObjectUtil.isNotEmpty(request.getHeader(RequestParamUtils.CONTENT_TYPE))){
            if (request.getHeader(RequestParamUtils.CONTENT_TYPE).equals(RequestParamUtils.CONTENT_TYPE_XML)){
                cn.hutool.json.JSONObject object = new cn.hutool.json.JSONObject(r);
                try {
                    response.getWriter().write(DocumentManagementUtils.addXmlLabel(JSONUtil.toXmlStr(object)));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
            }
        }
        return r;
    }
}