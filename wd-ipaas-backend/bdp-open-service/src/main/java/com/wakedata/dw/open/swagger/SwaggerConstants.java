package com.wakedata.dw.open.swagger;

/**
 * Swagger相关常量
 *
 * @author chenshaopeng
 * @date 2021/11/1
 */
public interface SwaggerConstants {

    /**
     * jsonSchema默认初始层级
     */
    String SCHEMA_ROOT = "{\"root\":%s}";

    /**
     * jsonSchema头节点名称
     */
    String FIRST_NAME = "root";

    /**
     * Swagger Json内部关键Key定义
     */
    String
        VERSIONS_KEY = "swagger"
        , DEFINE_KEY = "definitions"
        , PATHS_KEY = "paths"
        , REFER_KEY = "$ref"
        , REFER_V3_KEY = "$refer"
        , PARAMS_KEY ="parameters"
        , FORMAT_KEY = "format"
        , SCHEMA_KEY = "schema"
        , TYPE_KEY = "type"
        , HOST_KEY = "host"
        , NORMAL_CODE_KEY = "200"
        , RESPONSES_KEY = "responses"
        , SUMMARY_KEY = "summary"
        , TAGS_KEY = "tags"
        , NAME_KEY = "name"
        , REQUIRED_KEY = "required"
        , IN_KEY = "in"
        , PROPERTIES_KEY = "properties"
        , ITEMS_KEY = "items"
        , DESCRIPTION_KEY = "description"
        , ITEMS_PROPERTIES_KEY = "items.properties"
        , NORMAL_RESPONSES_KEY = "responses.200"
        , INFO_TITLE_KEY = "info.title"
        , INFO_DESC_KEY = "info.description"
        , BASE_PATH_KEY = "basePath"
        , SCHEMA_TYPE_KEY = "type|schema.type"
        , SERVICE_URL_KEY = "info.contact.url|info.termsOfService";

    /**
     * OpenApi Json内部关键Key定义
     */
    String
        OPEN_API_KEY = "openapi"
        , COMPONENTS_SCHEMAS_KEY = "components.schemas"
        , COMPONENTS_KEY = "components"
        , REQUEST_BODY_KEY = "requestBody"
        , REQUEST_BODY_CONTENT_APPLICATION_JSON_KEY = "requestBody.content.application/json"
        , OPEN_API_NORMAL_RESPONSES_KEY = "responses.200.content.application/json";

    /**
     * Swagger基础类型定义
     */
    String
        STRING = "string"
        , NUMBER = "number"
        , BOOLEAN = "boolean"
        , ARRAY = "array"
        , JSON_VAL = "json"
        , DATE_TIME = "date-time"
        , DATETIME = "dateTime"
        , INTEGER = "integer"
        , LONG = "long"
        , DOUBLE = "double"
        , OBJECT = "object";

    /**
     * 路径参数
     */
    String URL_PATH_PARAM = "path";

    /**
     * Swagger自定义父节点key
     */
    String FATHER_NODE_KEY = "father_node";
    String FATHER_NODE_NAME = "father_node_name";
    String APPEARED_NODE_KEY = "appeared_node";


    /**
     * Swagger不解析key列表
     */
    String[] INVALID_KEYS = {
            "originalRef"
            ,"responsesObject"
            ,"responseSchema"
            ,"x-apifox-orders"
            ,"x-apifox-ignore-properties"
            ,"x-apifox-folder"
    };

    /**
     * Swagger doc内容错误key
     */
    String ERROR_KEY = "error";

}
