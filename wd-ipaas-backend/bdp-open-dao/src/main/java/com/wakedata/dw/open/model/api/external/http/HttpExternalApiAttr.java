package com.wakedata.dw.open.model.api.external.http;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.wakedata.dw.open.model.api.AbstractApiAttr;

import java.util.List;
import javax.persistence.Column;
import javax.persistence.Table;
import javax.persistence.Transient;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author ZhangXueJun
 * @title RegisterHttp
 * @date 2021/3/1 15:15
 * @projectName dw-open
 * @description
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Table(name = "dw_open_api_http_attr")
public class HttpExternalApiAttr extends AbstractApiAttr {

    public static String ALL_FIELD = "__ALL__";

    /**
     * 后台服务Host
     */
    @Column(name = "host")
    private String host;

    /**
     * 后台服务Path
     */
    @Column(name = "path")
    private String path;

    /**
     * 超时时间，单位毫秒
     */
    @Column(name = "timeout")
    private Integer timeout;

    /**
     * 返回结果示例
     */
    @Column(name = "result_example")
    private String resultExample;

    /**
     * 返回编码示例
     */
    @Column(name = "error_example")
    private String errorExample;

    /**
     * 错误码定义
     */
    @JsonIgnore
    @Column(name = "error_definition_json")
    private String errorDefinitionJson;

    /**
     * WebService请求方法（API类型为WebService才会有此数据）
     */
    @Column(name = "ws_method")
    private String wsMethod;

    /**
     * WebService命名空间（API类型为WebService才会有此数据）
     */
    @Column(name = "ws_namespace_uri")
    private String wsNameSpaceUri;

    private List<HttpCode> httpCodes;

    /**
     * 返回结果（可解析）
     */
    @Transient
    private String responseResult;

    /**
     * 返回结果(jsonSchema)
     */
    @Transient
    private String responseJsonSchema;
}