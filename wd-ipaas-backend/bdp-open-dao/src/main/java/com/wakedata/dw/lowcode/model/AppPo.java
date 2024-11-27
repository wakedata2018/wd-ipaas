package com.wakedata.dw.lowcode.model;

import javax.persistence.Column;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author wanghu@wakedata.com
 * @title 应用信息
 * @date 2021/11/24
 * @since v1.0.0
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class AppPo extends OperatorPo {

    /**
     * 应用ID
     */
    @Column(name = "app_id")
    private Integer appId;

    /**
     * 租户ID
     */
    @Column(name = "ep_id")
    private Integer epId;

}
