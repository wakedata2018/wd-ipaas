package com.wakedata.dw.open.datasource.model;

import com.wakedata.dw.open.enums.ApiConditionDefaultColumnEnum;
import com.wakedata.dw.open.enums.DataTypeEnum;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author yiyufeng
 * @title DatasourceTableColumnDo
 * @projectName bdp-open
 * @date
 * @description
 */
@Data
public class DatasourceTableColumnDo {

    /**
     * 表名
     */
    @ApiModelProperty("表名")
    private String datasourceTableName;

    /**
     * 字段名
     */
    @ApiModelProperty("字段名")
    private String datasourceTableColumnName;

    /**
     * 字段类型
     */
    @ApiModelProperty("字段类型")
    private String datasourceTableColumnType;

    /**
     * 字段描述
     */
    @ApiModelProperty("字段描述")
    private String datasourceTableColumnDesc;

    /**
     * 字段长度
     */
    @ApiModelProperty("字段长度")
    private String datasourceTableColumnLength;

    /**
     * 别名
     */
    @ApiModelProperty("别名")
    private String alias;

    /**
     * 示例值
     */
    @ApiModelProperty("示例值")
    private String sample;

    /**
     * 是否必填
     */
    @ApiModelProperty("是否必填，true：必填、false：不必填")
    private Boolean required;

    /**
     * 是否设置了递增
     */
    @ApiModelProperty("是否递增")
    private Boolean isAutoIncrement;

    @ApiModelProperty("是否是动态参数 true/false")
    private Boolean isDynamicParam;

    @ApiModelProperty("是否允许为空 true/false")
    private Boolean allowEmpty;

    public DatasourceTableColumnDo() {
    }

    public DatasourceTableColumnDo(String datasourceTableColumnName, String datasourceTableColumnType, Boolean required, Boolean isDynamicParam) {
        this.datasourceTableColumnName = datasourceTableColumnName;
        this.datasourceTableColumnType = datasourceTableColumnType;
        this.required = required;
        this.isDynamicParam = isDynamicParam;
        this.allowEmpty = isDynamicParam;
    }

    public DatasourceTableColumnDo(String columnName, String columnType, String columnDesc, String sample, Boolean required) {
        this.datasourceTableColumnName = columnName;
        this.datasourceTableColumnType = columnType;
        this.datasourceTableColumnDesc = columnDesc;
        this.sample = sample;
        this.required = required;
    }

    public DatasourceTableColumnDo(String tableName, String columnName, String columnType, String columnDesc, String columnLength, String alias) {
        this.datasourceTableName = tableName;
        this.datasourceTableColumnName = columnName;
        this.datasourceTableColumnType = columnType;
        this.datasourceTableColumnDesc = columnDesc;
        this.datasourceTableColumnLength = columnLength;
        this.alias = alias;
    }
    public static DatasourceTableColumnDo build(
            String datasourceTableName,
            String datasourceTableColumnName,
            String datasourceTableColumnType,
            String datasourceTableColumnDesc,
            String datasourceTableColumnLength,
            String alias) {
        DatasourceTableColumnDo datasourceTableColumnDo = new DatasourceTableColumnDo();
        datasourceTableColumnDo.setDatasourceTableName(datasourceTableName);
        datasourceTableColumnDo.setDatasourceTableColumnName(datasourceTableColumnName);
        datasourceTableColumnDo.setDatasourceTableColumnType(datasourceTableColumnType);
        datasourceTableColumnDo.setDatasourceTableColumnDesc(datasourceTableColumnDesc);
        datasourceTableColumnDo.setDatasourceTableColumnLength(datasourceTableColumnLength);
        datasourceTableColumnDo.setAlias(alias);
        return datasourceTableColumnDo;
    }

    /**
     * 根据API访问默认参数枚举类、API ID、样例数据构建DatasourceTableColumnDo对象
     *
     * @param columnEnum     API访问默认参数枚举类
     * @return DatasourceTableColumnDo
     */
    public static DatasourceTableColumnDo buildRequestParam(String datasourceTableName, ApiConditionDefaultColumnEnum columnEnum) {
        return DatasourceTableColumnDo.build(
                datasourceTableName,
                columnEnum.getAssetColumns(),
                columnEnum.getAssetDatatype(),
                columnEnum.getDescriptions(),
                String.valueOf(columnEnum.getAssetColumnsLength()),
                null
        );
    }

    /**
     * 根据API访问默认参数枚举类、API ID、样例数据构建DatasourceTableColumnDo对象（可自定义参数类型）
     *
     * @param columnEnum     API访问默认参数枚举类
     * @return DatasourceTableColumnDo
     */
    public static DatasourceTableColumnDo buildRequestParam(String datasourceTableName, ApiConditionDefaultColumnEnum columnEnum, DataTypeEnum assetDatatype,String sample) {
        DatasourceTableColumnDo columnDo = DatasourceTableColumnDo.build(
                datasourceTableName,
                columnEnum.getAssetColumns(),
                assetDatatype.getType(),
                columnEnum.getDescriptions(),
                String.valueOf(columnEnum.getAssetColumnsLength()),
                null
        );
        columnDo.setSample(sample);
        return columnDo;
    }
}
