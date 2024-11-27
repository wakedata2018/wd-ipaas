package com.wakedata.dw.open.service.api.dto;

import com.alibaba.fastjson.annotation.JSONField;
import com.wakedata.common.core.hashids.annotation.HashidsConvert;
import com.wakedata.dw.open.enums.DataAssetEnums;
import com.wakedata.dw.open.parammapping.HttpParamKind;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * @author tanzhi
 * @title ApiConditionPo
 * @projectName bdp-open
 * @date 2019/8/19 15:26
 */
@Data
public class ApiConditionDTO implements Serializable {
    /**
     * id
     */
    private Integer id;
    /**
     * apiId
     */
    @HashidsConvert
    private Integer dataAssetId;
    /**
     * 属性名
     */
    private String assetColumns;
    /**
     * 属性类型
     */
    private String assetDatatype;
    /**
     * 属性长度限制
     */
    private Integer assetColumnsLength;
    /**
     * 描述
     */
    private String descriptions;
    /**
     * 数据类型 请求还是响应
     */
    private DataAssetEnums.FiledTypeEnums type;

    private DataAssetEnums.FiledTypeAttrEnums typeAttr;

    /**
     * 参数类型
     */
    private HttpParamKind httpParamKind;

    /**
     * 示例值
     */
    private String sample;
    /**
     * 是否必填
     */
    private Boolean required;

    private Boolean multiValue;

    /**
     * schema数据串
     */
    private String jsonSchema;

    private Boolean isSchema;

    @JSONField(format="yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    @JSONField(format="yyyy-MM-dd HH:mm:ss")
    private Date updateTime;
    /**
     * schema解析数据列
     */
    private List<ApiRespParamDTO> schemaParseDataList;

}