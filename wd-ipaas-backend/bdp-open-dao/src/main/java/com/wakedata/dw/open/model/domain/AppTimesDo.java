package com.wakedata.dw.open.model.domain;

import lombok.Data;

/**
 * @author wq
 * @title AppTimesDo
 * @date 2020/9/8 17:56
 * @projectName bdp-open
 * @description
 */
@Data
public class AppTimesDo {
    /**
     * 接入名称
     */
    private String appName;
    /**
     * 负责人
     */
    private String inCharge;
    /**
     * 次数
     */
    private String num;
}
