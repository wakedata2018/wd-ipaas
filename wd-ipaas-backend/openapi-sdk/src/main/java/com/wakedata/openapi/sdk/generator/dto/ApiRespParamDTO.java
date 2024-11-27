package com.wakedata.openapi.sdk.generator.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

/**
 * @author wanghu@wakedata.com
 * @title API返回参数DTO
 * @date 2021/12/13
 * @since v1.0.0
 */
@Getter
@Setter
@ToString
//@ApiModel("API返回参数DTO")
public class ApiRespParamDTO {

    /**
     * 自增ID
     */
//    @ApiModelProperty("自增ID")
    private Integer id;

    /**
     * 列名称
     */
//    @ApiModelProperty("列名称")
    private String assetColumns;

    /**
     * 列数据类型[如string]
     */
//    @ApiModelProperty("列数据类型[如string]")
    private String assetDataType;

    /**
     * 字段描述
     */
//    @ApiModelProperty("字段描述")
    private String description;

    /**
     * 响应类型[HEAD BODY]
     *
     * @see HttpParamKind
     */
//    @ApiModelProperty("响应类型[HEAD BODY]")
    private HttpParamKind type;

    /**
     * 子返回参数
     */
//    @ApiModelProperty("子返回参数")
    List<ApiRespParamDTO> childApiRespParams;

    /**
     * 列长度
     */
    private Integer assetColumnLength;

    /**
     * 是否是数据表类型
     */
//    @ApiModelProperty("是否是数据表类型")
    private Boolean normalTable;

    /**
     * 响应业务类型，0：普通http响应模板，1：流程编排结果响应模板
     */
//    @ApiModelProperty("响应业务类型，，0-HTTP_RESULT：普通http响应模板，1-LITEFLOW_RESULT：流程编排结果响应模板")
//    private DataAssetEnums.ApiResponseBusinessType businessType;

    /**
     * 参数值类型，fixed：固定值，reference：引用值,function:函数
     */
//    @ApiModelProperty("参数值类型，fixed：固定值，reference：引用值,function:函数")
    private String paramValueType;
    /**
     * 默认值
     */
//    @ApiModelProperty("默认值， 1.新增或者更新时用于存储响应头或者响应体的默认值(入库) 2.流程编排时作为返回参数模版的固定值")
    private String defaultValue;
    /**
     * 返回模板jsonpath表达式
     */
//    @ApiModelProperty("返回模板jsonpath表达式")
    private String expression;

    /**
     * 返回字段类型:string,integer,boolean,array,object,number
     */
//    @ApiModelProperty("返回字段类型:string,integer,boolean,array,object,number")
    private String resultColumnType;

//    @ApiModelProperty("parentId")
    private Integer parentId;

    /**
     * 节点id
     */
//    @ApiModelProperty("节点id")
    private String nodeId;

    /**
     * 节点名称
     */
//    @ApiModelProperty("节点名称")
    private String nodeName;

//    @ApiModelProperty("响应模版字符串：jsonSchema/xml")
    private String responsePostData;

//    @ApiModelProperty("是否为schema格式：true/false")
    private Boolean isSchema;
}
