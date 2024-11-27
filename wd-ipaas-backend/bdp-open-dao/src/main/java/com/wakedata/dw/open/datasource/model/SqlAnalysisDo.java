package com.wakedata.dw.open.datasource.model;

import com.wakedata.dw.open.enums.DataAssetEnums;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author wq
 * @title SqlAnalysisDo
 * @date 2020/12/1 19:13
 * @projectName dw-open
 * @description
 */
@Data
@ApiModel(value = "解析SQL返回结果", description = "解析SQL返回结果")
public class SqlAnalysisDo {
    /**
     * 请求参数
     */
    @ApiModelProperty("请求参数列表")
    private List<DatasourceTableColumnDo> requestParam;
    /**
     * 解析SQL得到的返回参数，放置在data公共参数中
     */
    @ApiModelProperty("解析SQL得到的返回参数，放置在data公共参数中")
    private List<DatasourceTableColumnDo> responseParam;
    /**
     * 公共的返回参数
     */
    @ApiModelProperty("公共的返回参数")
    private List<DatasourceTableColumnDo> baseResponseParam;
    /**
     * SQL操作类型
     */
    @ApiModelProperty("SQL操作类型")
    private DataAssetEnums.DataApiOperationType sqlOperationType;
}
