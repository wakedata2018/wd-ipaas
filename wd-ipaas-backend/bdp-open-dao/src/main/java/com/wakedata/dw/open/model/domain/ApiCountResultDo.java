package com.wakedata.dw.open.model.domain;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @author wq
 * @title ApiCountResultDo
 * @date 2020/9/8 15:32
 * @projectName bdp-open
 * @description
 */
@Data
public class ApiCountResultDo {
    /**
     * 日期
     */
    private String cusDate;
    /**
     * 数量
     */
    private BigDecimal num;
}
