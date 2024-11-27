package com.wakedata.dw.open.model.domain;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @author wq
 * @title ApiTimesDo
 * @date 2020/9/8 11:05
 * @projectName bdp-open
 * @description
 */
@Data
public class ApiTimesDo {
    /**
     * api总数量
     */
    private BigDecimal totalNum;
    /**
     * api错误数量
     */
    private BigDecimal errorNum;
}
