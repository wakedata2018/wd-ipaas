package com.wakedata.openapi.sdk.generator;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * 代码模板地址
 *
 * @author luomeng
 * @date 2022/10/25 18:56
 */
public class CodeTemplate {

    /**
     * 项目名占位符
     */
    public static String TEMPLATE_PROJECT_NAME = "{projectName}";

    /**
     * api分组包名占位符
     */
    public static String TEMPLATE_API_GROUP_PACKAGE = "{apiGroupPackage}";

    /**
     * api包名占位符
     */
    public static String TEMPLATE_API_PACKAGE = "{apiPackage}";


    /**
     * api类名占位符
     */
    public static String TEMPLATE_API_CLASS_NAME = "{apiClassName}";

    /**
     * 模板文件后缀名
     */
    public static String TEMPLATE_FILE_SUFFIX = ".vm";

    /**
     * 模板路径-目录
     */
    public static String TEMPLATE_DIR = "templates" + File.separator;

    /**
     * 项目目录
     */
    public static String TEMPLATE_PROJECT_DIR = TEMPLATE_PROJECT_NAME + "-openapi-sdk" + File.separator;

    /**
     * 项目代码目录
     */
    public static String TEMPLATE_PROJECT_CODE_DIR = "src" + File.separator + "main" + File.separator + "java" + File.separator + "com" + File.separator + "wakedata" + File.separator + "openapi" + File.separator + "sdk" + File.separator;

    /**
     * api代码目录
     */
    public static String TEMPLATE_PROJECT_CODE_API_DIR = "api" + File.separator + TEMPLATE_API_GROUP_PACKAGE + File.separator + TEMPLATE_API_PACKAGE + File.separator;

    /**
     * xml配置文件模板
     */
    public static List<String> XML_TEMPLATE = Arrays.asList("pom.xml.vm","Readme.md.vm");

    /**
     * 公共文件模板
     */
    public static List<String> COMMON_TEMPLATE = Arrays.asList("accesstoken" + File.separator + "AccessToken.java.vm", "accesstoken" + File.separator + "AccessTokenUtil.java.vm"
            , "common" + File.separator + "OpenApiConstant.java.vm", "common" + File.separator + "OpenApiRequestUtil.java.vm", "common" + File.separator + "OpenApiUrl.java.vm", "common" + File.separator + "SignUtil.java.vm","common"+ File.separator + "TypeConvertUtil.java.vm"
            , "exception" + File.separator + "OpenApiException.java.vm"
            , "result" + File.separator + "OpenApiPageResultDTO.java.vm", "result" + File.separator + "OpenApiResultDTO.java.vm"
            , "MainTest.java.vm", "OpenApiExample.java.vm");

    /**
     * api模板
     */
    public static List<String> API_TEMPLATE = Arrays.asList(TEMPLATE_API_CLASS_NAME + ApiTemplateEnum.APIEXEC_TEMPLATE.getTemplate()
            ,TEMPLATE_API_CLASS_NAME + ApiTemplateEnum.REQUEST_BODY_TEMPLATE.getTemplate()
            ,TEMPLATE_API_CLASS_NAME + ApiTemplateEnum.REQUEST_HEAD_TEMPLATE.getTemplate()
            ,TEMPLATE_API_CLASS_NAME + ApiTemplateEnum.REQUEST_PARAM_TEMPLATE.getTemplate()
            ,TEMPLATE_API_CLASS_NAME + ApiTemplateEnum.RESPONSE_BODY_TEMPLATE.getTemplate()
            ,TEMPLATE_API_CLASS_NAME + ApiTemplateEnum.RESPONSE_HEAD_TEMPLATE.getTemplate());

    /**
     * api模板枚举
     */
    public enum ApiTemplateEnum{

        /**
         * 模板
         */
        APIEXEC_TEMPLATE("ApiExec.java.vm"),
        REQUEST_BODY_TEMPLATE("RequestBody.java.vm"),
        REQUEST_HEAD_TEMPLATE("RequestHead.java.vm"),
        REQUEST_PARAM_TEMPLATE("RequestParam.java.vm"),
        RESPONSE_BODY_TEMPLATE("ResponseBody.java.vm"),
        RESPONSE_HEAD_TEMPLATE("ResponseHead.java.vm")
        ;
        /**
         * 模板名称
         */
        private String template;


        ApiTemplateEnum(String template) {
            this.template = template;
        }

        public String getTemplate() {
            return template;
        }

    }
}
