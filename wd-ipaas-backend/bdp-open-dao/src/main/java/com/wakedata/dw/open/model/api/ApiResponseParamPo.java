package com.wakedata.dw.open.model.api;

import com.wakedata.dw.open.enums.DataAssetEnums;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.wakedata.dw.open.model.OpenApiOperatorPo;
import com.wakedata.dw.open.parammapping.HttpParamKind;
import com.wakedata.dw.open.parammapping.ParamMappingTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * API响应参数(DwOpenApiResponseParam)实体类
 *
 * @author wanghu
 * @since 2021-12-10 10:16:20
 */
@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "dw_open_api_response_param")
public class ApiResponseParamPo extends OpenApiOperatorPo {

    /**
     * 自增ID
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    /**
     * 数据资产ID
     */
    @Column(name = "data_asset_api_id")
    private Integer dataAssetId;

    /**
     * 列名称
     */
    @Column(name = "asset_column")
    private String assetColumns;

    /**
     * 列数据类型[如string]
     */
    @Column(name = "asset_data_type")
    private String assetDataType;

    /**
     * 列长度
     */
    @Column(name = "asset_column_length")
    private Integer assetColumnLength;

    /**
     * 是否有值[0-响应, 1-无响应]
     */
    @Column(name = "responded")
    private Integer responded;

    /**
     * 字段描述
     */
    @Column(name = "description")
    private String description;

    /**
     * 响应类型[0-body;1-head]
     *
     * @see HttpParamKind
     */
    @Column(name = "type")
    private HttpParamKind type;

    /**
     * 父级字段id
     */
    @Column(name = "parent_id")
    private Integer parentId;

    /**
     * 创建者
     */
    @Column(name = "create_by")
    private String createBy;

    /**
     * 最后更新者
     */
    @Column(name = "update_by")
    private String updateBy;

    /**
     * 响应业务类型，0：普通http响应模板，1：流程编排结果响应模板
     */
    @Column(name = "business_type")
    private DataAssetEnums.ApiResponseBusinessType businessType;

    /**
     * 参数值类型，fixed：固定值，reference：引用值,function:函数
     * {@link ParamMappingTypeEnum}
     */
    @Column(name = "param_value_type")
    private String paramValueType;
    /**
     * 默认值
     */
    @Column(name = "default_value")
    private String defaultValue;
    /**
     * 返回模板jsonpath表达式
     */
    @Column(name = "expression")
    private String expression;
    /**
     * 结果返回字段类型:string,integer,boolean,array,object,number
     */
    @Column(name = "result_column_type")
    private String resultColumnType;

    /**
     * 响应体内容
     */
    @Column(name = "response_post_data")
    private String responsePostData;

    /**
     * 响应体内容格式
     */
    @Column(name = "is_schema")
    private Boolean isSchema;

}

