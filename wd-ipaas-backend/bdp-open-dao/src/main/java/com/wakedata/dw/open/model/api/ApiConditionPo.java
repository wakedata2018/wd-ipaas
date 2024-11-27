package com.wakedata.dw.open.model.api;

import com.wakedata.dw.open.enums.ApiConditionDefaultColumnEnum;
import com.wakedata.dw.open.enums.DataAssetEnums;
import com.wakedata.dw.open.model.BasePo;
import com.wakedata.dw.open.parammapping.HttpParamKind;
import lombok.Data;

import javax.persistence.*;
import java.util.Objects;

/**
 * @author tanzhi
 * @title ApiConditionPo
 * @projectName bdp-open
 * @date 2019/8/19 15:26
 */
@Table(name = "dw_open_api_condition")
@Data
public class ApiConditionPo extends BasePo {
    /**
     * 主键id
     */
    @Column(name = "id")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    /**
     * ApiId
     */
    @Column(name = "data_asset_api_id")
    private Integer dataAssetId;
    /**
     * 参数名称
     */
    @Column(name = "asset_columns")
    private String assetColumns;
    /**
     * 数据类型
     */
    @Column(name = "asset_datatype")
    private String assetDatatype;
    /**
     * 参数长度
     */
    @Column(name = "asset_columns_length")
    private Integer assetColumnsLength;
    /**
     * 参数描述
     */
    @Column(name = "descriptions")
    private String descriptions;
    /**
     * 参数类型（1-PARAMETERS-请求参数，2-RESULT-返回结果）
     */
    @Column(name = "type")
    private DataAssetEnums.FiledTypeEnums type;
    /**
     * 入参属性（0-OPERATOR， 1-FILTER）
     */
    @Column(name = "type_attr")
    private DataAssetEnums.FiledTypeAttrEnums typeAttr;
    /**
     * 参数值位置(QUERY, BODY, HEAD...)
     */
    @Column(name = "param_kind")
    private HttpParamKind httpParamKind;
    /**
     * 示例值
     */
    @Column(name = "sample")
    private String sample;
    /**
     * 是否必传
     */
    @Column(name = "required")
    private Boolean required;
    /**
     * 是否支持多值
     */
    @Column(name = "multi_value")
    private Boolean multiValue;
    /**
     * JSONSchema格式的值
     */
    @Column(name = "json_schema")
    private String jsonSchema;
    /**
     * 是否JSONSchema格式
     */
    @Column(name = "is_schema")
    private Boolean isSchema;

    /**
     * 是否允许为空
     */
    @Column(name = "allow_empty")
    private Boolean allowEmpty;

    /**
     * 默认输入参数标识
     */
    @Transient
    private Boolean defaultInputParam;

    /**
     * 是否自动解析
     */
    private boolean autoPare = true;

    public static ApiConditionPo build(
            Integer dataAssetApiId,
            String column,
            String description,
            String dataType,
            HttpParamKind httpParamKind,
            Integer length,
            DataAssetEnums.FiledTypeEnums enums,
            String sample,
            Boolean required,
            Boolean multi) {
        ApiConditionPo apiCondition = build(
                dataAssetApiId,
                column,
                description,
                dataType,
                length,
                enums,
                sample,
                required,
                multi
        );
        apiCondition.httpParamKind = httpParamKind;
        return apiCondition;
    }

    public static ApiConditionPo build(
            Integer dataAssetApiId,
            String column,
            String description,
            String dataType,
            Integer length,
            DataAssetEnums.FiledTypeEnums enums,
            String sample,
            Boolean required,
            Boolean multi) {
        ApiConditionPo apiCondition = new ApiConditionPo();
        apiCondition.setDataAssetId(dataAssetApiId);
        apiCondition.setAssetColumns(column);
        apiCondition.setDescriptions(description);
        apiCondition.setAssetDatatype(dataType);
        apiCondition.setAssetColumnsLength(length);
        apiCondition.setHttpParamKind(HttpParamKind.QUERY);
        apiCondition.setType(enums);
        apiCondition.setSample(sample);
        apiCondition.setRequired(required);
        apiCondition.setMultiValue(multi);
        return apiCondition;
    }

    public static ApiConditionPo buildRequestParam(
            Integer dataAssetApiId,
            String column,
            String description,
            String dataType,
            Integer length,
            String sample,
            Boolean required,
            Boolean multi,
            Boolean allowEmpty) {
        ApiConditionPo apiCondition = ApiConditionPo.build(
                dataAssetApiId,
                column,
                description,
                dataType,
                length,
                DataAssetEnums.FiledTypeEnums.PARAMETERS,
                sample, required, multi
        );
        //设置默认参数为true
        apiCondition.setDefaultInputParam(Boolean.TRUE);
        apiCondition.setAllowEmpty(allowEmpty);
        return apiCondition;
    }

    /**
     * 根据API访问默认参数枚举类、API ID、样例数据构建ApiConditionPo对象
     *
     * @param columnEnum     API访问默认参数枚举类
     * @param sample         样例
     * @param dataAssetApiId API ID
     * @return ApiConditionPo
     */
    public static ApiConditionPo buildRequestParam(ApiConditionDefaultColumnEnum columnEnum, String sample, Integer dataAssetApiId) {
        ApiConditionPo apiCondition = ApiConditionPo.build(
                dataAssetApiId,
                columnEnum.getAssetColumns(),
                columnEnum.getDescriptions(),
                columnEnum.getAssetDatatype(),
                columnEnum.getAssetColumnsLength(),
                DataAssetEnums.FiledTypeEnums.PARAMETERS,
                sample,
                columnEnum.getRequired(),
                columnEnum.getMultiValue()
        );
        //设置默认参数为true
        apiCondition.setDefaultInputParam(Boolean.TRUE);
        return apiCondition;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ApiConditionPo that = (ApiConditionPo) o;
        return Objects.equals(assetColumns, that.assetColumns) &&
                type == that.type &&
                httpParamKind == that.httpParamKind;
    }

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