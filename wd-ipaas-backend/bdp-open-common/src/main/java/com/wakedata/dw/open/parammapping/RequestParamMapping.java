package com.wakedata.dw.open.parammapping;

import lombok.Data;

/**
 * 请求参数定义
 *
 * @author ZhangXueJun
 * @title FieldMapping
 * @date 2021/3/29 11:43
 * @projectName bdp-open
 * @description
 */
@Data
public class RequestParamMapping extends AbstractParamMapping {

//    @JsonIgnore
    private HttpParamKind httpParamKind = HttpParamKind.QUERY;


    public RequestParamMapping() {
    }

    public RequestParamMapping(String field, String expression) {
        super(field, expression);
    }
}