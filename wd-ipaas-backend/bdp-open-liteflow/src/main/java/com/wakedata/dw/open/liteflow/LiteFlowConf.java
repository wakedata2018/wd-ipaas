package com.wakedata.dw.open.liteflow;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * 服务编排配置
 *
 * @author ZhangXueJun
 * @title LiteflowConf
 * @date 2021/5/7 10:52
 * @projectName dw-open
 * @description
 */
@Slf4j
@Configuration
@ConfigurationProperties(prefix = "liteflow")
@Data
public class LiteFlowConf {
    private int slotSize = 2048;
    private int whenMaxWaitSecond = 2048;
    private int whenMaxWorkers = 4;
    private int whenQueueLimit = 512;
}
