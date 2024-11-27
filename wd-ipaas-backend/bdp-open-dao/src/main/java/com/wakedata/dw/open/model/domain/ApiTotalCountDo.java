package com.wakedata.dw.open.model.domain;

import lombok.Data;

/**
 * @author wq
 * @title ApiTotalCountDo
 * @date 2020/9/17 17:51
 * @projectName dw-open
 * @description
 */
@Data
public class ApiTotalCountDo {
    /**
     * api历史调用次数
     */
    private Integer historyCount;
    /**
     * api今日调用次数
     */
    private Integer todayCount;
    /**
     * 今日调用成功次数
     */
    private Integer todaySuccess;
    /**
     * 今日调用失败次数
     */
    private Integer todayFailed;
}
