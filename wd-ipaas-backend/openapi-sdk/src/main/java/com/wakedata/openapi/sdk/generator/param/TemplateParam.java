package com.wakedata.openapi.sdk.generator.param;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * 模板参数
 * @author luomeng
 * @date 2022/10/26 19:45
 */
@Data
@NoArgsConstructor
public class TemplateParam implements Serializable {

    /**
     * api参数
     */
    public static final String TEMPLATE_PARAM_API = "api";

    /**
     * 项目名
     */
    private String projectName;

    /**
     * 项目版本号
     */
    private String projectVersion;

    /**
     * 应用key
     */
    private String appKey;

    /**
     * 应用密钥
     */
    private String appSecret;

    /**
     * 生成时间
     */
    private String dateTime;

    /**
     * 请求域名 - 不同环境域名不同
     */
    private String apiRequestHost;

    /**
     * api分组列表
     */
    private List<ApiGroup> apiGroupList;

    /**
     * api信息 ，生成api请求时需填入
     */
    private Api api;

}
