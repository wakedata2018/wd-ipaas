package com.wakedata.dw.open.service.impl.dremio;

import lombok.Data;

@Data
public class DremioRestConfig {


    /**
     * 最大连接数
     */
    private Integer maxSize = 20;

    /**
     * 响应超时
     */
    private Integer responseTimeout = 30000;

    /**
     * 连接超时
     */
    private Integer connectionTimeout = 30000;

    /**
     * 连接池获取超时
     */
    private Integer requestTimeout = 30000;
}