package com.wakedata.dw.open.service.vo;

import com.wakedata.common.core.hashids.annotation.HashidsConvert;
import com.wakedata.dw.open.enums.DataAssetEnums;
import com.wakedata.dw.open.model.BasePo;
import com.wakedata.dw.open.parammapping.HttpParamKind;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Objects;

/**
 * @author zhengqinghui@wakedata.com
 * @date 2022/9/1 16:31
 */
@Data
@ApiModel(value = "API访问参数", description = "API访问参数;1、api信息对应的访问参数配置")
public class DocumentApiConditionVo extends BasePo {

    @ApiModelProperty(value = "自增Id")
    private Integer id;

    @HashidsConvert
    @ApiModelProperty(value = "数据资产Id")
    private Integer dataAssetId;

    @ApiModelProperty(value = "对应列")
    private String assetColumns;

    @ApiModelProperty(value = "数据类型")
    private String assetDatatype;

    @ApiModelProperty(value = "长度")
    private Integer assetColumnsLength;

    @ApiModelProperty(value = "描述")
    private String descriptions;

    @ApiModelProperty(value = "参数类型枚举")
    private DataAssetEnums.FiledTypeEnums type;

    @ApiModelProperty(value = "字段类型Attr枚举")
    private DataAssetEnums.FiledTypeAttrEnums typeAttr;

    @ApiModelProperty(value = "Http参数类型")
    private HttpParamKind httpParamKind;

    @ApiModelProperty(value = "示例")
    private String sample;

    @ApiModelProperty(value = "是否必须")
    private Boolean required;

    @ApiModelProperty(value = "是否支持多值")
    private Boolean multiValue;

    @ApiModelProperty(value = "json_schema")
    private String jsonSchema;

    @ApiModelProperty(value = "是否为schema格式：0不是 1是")
    private Boolean isSchema;

    /**
     * 默认输入参数标识
     */
    @ApiModelProperty(value = "默认输入参数标识")
    private Boolean defaultInputParam;

    /**
     * 是否自动解析
     */
    @ApiModelProperty(value = "是否自动解析")
    private boolean autoPare = true;
    

    @Override
    public int hashCode() {
        return Objects.hash(assetColumns, type, httpParamKind);
    }

    @Override
    public String toString() {
        return "ApiConditionPo{" +
                "id=" + id +
                ", dataAssetId=" + dataAssetId +
                ", assetColumns='" + assetColumns + '\'' +
                ", assetDatatype='" + assetDatatype + '\'' +
                ", assetColumnsLength=" + assetColumnsLength +
                ", descriptions='" + descriptions + '\'' +
                ", type=" + type +
                ", httpParamKind=" + httpParamKind +
                ", sample='" + sample + '\'' +
                ", required=" + required +
                ", multiValue=" + multiValue +
                '}';
    }
}
