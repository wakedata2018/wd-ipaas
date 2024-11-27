package com.wakedata.dw.open.model.api.external.http;

import lombok.Data;

/**
 * @author ZhangXueJun
 * @title HttpExternalCode
 * @date 2021/3/6 14:54
 * @projectName dw-open
 * @description
 */
@Data
public class HttpCode {

    private String errorCode;
    private String errorMsg;
    private String solution;
}
