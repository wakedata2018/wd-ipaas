package com.wakedata.dw.open.model.query;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.NotNull;

/**
 * @author WangChenSheng
 * date 2022/8/24 16:12
 */
@Getter
@Setter
@ToString
@ApiModel("swaggerApi查询条件")
public class SwaggerApiQuery extends PageQuery {

    @ApiModelProperty("swaggerId")
    @NotNull(message = "swaggerId不能为空")
    private Integer swaggerId;

    @ApiModelProperty("API查找")
    private String apiFind;

    @ApiModelProperty("导入结果：-1 全部 0 未导入 1导入成功 2 导入失败")
    private Integer importStatus;
}
