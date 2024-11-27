package com.wakedata.dw.open.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;

/**
 * @author WangChenSheng
 * @descriptor
 * @title XxlJobDOConfig
 * @date 2022/10/22 15:34
 */
@RefreshScope
@Component
@Data
public class XxlJobTaskConfig {

    @Value("${xxl.mysql.host:}")
    private String host;

    @Value("${xxl.mysql.name:}")
    private String dbName;

    @Value("${xxl.mysql.username:}")
    private String userName;

    @Value("${xxl.mysql.password:}")
    private String password;

    @Value("${xxl.mysql.port:}")
    private Integer port;

    @Value("${xxl.group.id:}")
    private Integer jobGroupId;

}
