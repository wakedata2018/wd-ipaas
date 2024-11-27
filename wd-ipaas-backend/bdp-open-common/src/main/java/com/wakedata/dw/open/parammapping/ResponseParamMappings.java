package com.wakedata.dw.open.parammapping;

import lombok.Data;

/**
 * 响应参数定义
 *
 * @author ZhangXueJun
 * @title ResponseParam
 * @date 2021/3/30 11:51
 * @projectName bdp-open
 * @description
 */
@Data
public class ResponseParamMappings extends AbstractParamMapping {

    public ResponseParamMappings() {
    }

    public ResponseParamMappings(String field, String expression) {
        super(field, expression);
    }
}