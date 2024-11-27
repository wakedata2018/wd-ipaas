package com.wakedata.dw.open.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * dwopen公共配置
 * @author luomeng
 * @date 2022/8/6 16:21
 */
@RefreshScope
@Component
@Data
public class DwOpenCommonConfig {

    /**
     * 允许重放时间(s)
     */
    @Value("${ipaas.auth.replay.time:60}")
    private Long replayTime;

    /**
     * 平台管理员应用APP key
     */
    @Value("${ipaas.platform.admin.app.key:}")
    private String platformAdminAppKey;

    /**
     * 域名，API文档使用字段
     */
    @Value("${ipaas.open.api.document.domainName:}")
    private String domainName;

    /**
     * API对应文档链接
     */
    @Value("${ipaas.open.api.document.apiUrl:}")
    private String apiUrl;
    /**
     * httpclient 最大连接数
     */
    @Value("${ipaas.httpclient.maxtotal:600}")
    private Integer maxTotal;
    /**
     * httpclient 每个路由的最大连接数
     */
    @Value("${ipaas.httpclient.maxPerRoute:200}")
    private Integer maxPerRoute;

    /**
     * 读取超时时间
     */
    @Value("${ipaas.httpclient.readTimeout:60000}")
    private Integer readTimeout;
    /**
     * 连接超时时间
     */
    @Value("${ipaas.httpclient.connectTimeout:60000}")
    private Integer connectTimeout;
    /**
     * web不需要license许可过滤拦截的接口(以,隔开)
     */
    @Value("${dw.open.web.un-license.uri:}")
    private List<String> webUnLicenseUri;

    @Value("${ipaas.domain:}")
    private String ipaasDomain;

    /**
     * 平台管理员租户id
     */
    @Value("${ipaas.platform.lesseeId:1}")
    private Long platformLesseeId;
}
