package com.wakedata.dw.open.service.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * @author luomeng
 * @date 2022/8/8 19:33
 */
@Data
public class AppAccessAuthConfig implements Serializable {

    /**
     * 惟客云应用id
     */
    private Long id;
    /**
     * 惟客云租户id
     */
    private Long tenantId;
    /**
     * 名称
     */
    private String appName;

    /**
     * 惟客云应用id
     */
    private Long appBuId;

    public AppAccessAuthConfig() {
    }

    public AppAccessAuthConfig(Long tenantId, Long appBuId) {
        this.tenantId = tenantId;
        this.appBuId = appBuId;
    }
}
