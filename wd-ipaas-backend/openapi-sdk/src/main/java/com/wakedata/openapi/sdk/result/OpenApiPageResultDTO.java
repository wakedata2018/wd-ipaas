package com.wakedata.openapi.sdk.result;

import lombok.Data;


/**
 * 开放平台api带分页结果返回格式
 * @author luomeng
 * @date 2022/8/23 19:24
 */
@Data
public class OpenApiPageResultDTO<T> extends OpenApiResultDTO {

    /**
     * 每页记录数
     */
    private Integer pageSize;
    /**
     * 当前页
     */
    private Integer pageNo;
    /**
     * 总数
     */
    private Long totalCount;


}
