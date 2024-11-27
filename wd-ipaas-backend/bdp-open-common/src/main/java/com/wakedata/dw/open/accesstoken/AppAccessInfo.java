package com.wakedata.dw.open.accesstoken;

import lombok.Data;

import java.io.Serializable;

/**
 * @author luomeng
 * @Description 应用信息
 * @createTime 2022-08-07 21:07:00
 */
@Data
public class AppAccessInfo implements Serializable {

    /**
     * id
     */
    private Integer dataAccessAppId;

    /**
     * 应用名称
     */
    private String dataAccessAppName;

    /**
     * appKey
     */
    private String dataAccessKey;

    /**
     * appSecret
     */
    private String dataAccessSecret;

    /**
     * 租户id
     */
    private Long lesseeId;

    /**
     * 是否是api测试
     */
    private Boolean isApiTest = Boolean.FALSE;
}
