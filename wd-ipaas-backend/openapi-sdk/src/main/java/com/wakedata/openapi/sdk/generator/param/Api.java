package com.wakedata.openapi.sdk.generator.param;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * api模板参数
 *
 * @author luomeng
 * @date 2022/10/26 12:39
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Api implements Serializable {

    /**
     * url路径名称,全大写
     */
    private String pathName;

    /**
     * api类名，首字母大写，驼峰标识
     */
    private String className;

    /**
     * api描述
     */
    private String desc;

    /**
     * api地址
     */
    private String url;

    /**
     * 请求方式，全大写，GET/POST
     */
    private String method;

    /**
     * 分组包名，和apigroup.groupPackage一致
     */
    private String groupPackage;

    /**
     * 分组名 ，和apigroup.name一致
     */
    private String groupName;

    /**
     * api包名
     */
    private String apiPackage;

    /**
     * 响应头属性列表
     */
    private List<ApiField> respHeadList;

    /**
     * 响应体属性列表
     */
    private List<ApiField> respBodyList;
    /**
     * 请求头属性列表
     */
    private List<ApiField> reqHeadList;
    /**
     * 请求参数属性列表
     */
    private List<ApiField> reqParamList;
    /**
     * 请求体属性列表
     */
    private List<ApiField> reqBodyList;
    /**
     * 是否包含请求头
     */
    private Boolean containHead;

    /**
     * 是否包含请求参数
     */
    private Boolean containParam;

    /**
     * 是否包含请求体
     */
    private Boolean containBody;
    /**
     * 是否包含响应头
     */
    private Boolean containRespHead;
    /**
     * 是否包含响应体
     */
    private Boolean containRespBody;
}
