package com.wakedata.dw.open.datasource.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author zhengqinghui@wakedata.com
 * @date 2022/12/20 17:58
 */
@Data
public class DatasourceTableColumnVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 指定表下所有列信息
     */
    @ApiModelProperty(value = "所有列信息")
    private List<DatasourceTableColumnDo> datasourceTableColumnDoList;

    /**
     * 公共的返回参数
     */
    @ApiModelProperty(value = "公共的返回参数")
    private List<DatasourceTableColumnDo> baseResponseParam;
}
