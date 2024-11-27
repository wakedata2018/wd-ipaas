package com.wakedata.dw.open.service.api.dto;

import com.wakedata.dw.open.model.query.PageQuery;
import lombok.Data;

/**
 * 自定义函数查询
 * @author luomeng
 * @date 2022/11/3 14:21
 */
@Data
public class CustomFunctionQuery extends PageQuery {
    /**
     * 租户ID
     */
    private Long lesseeId;

}
