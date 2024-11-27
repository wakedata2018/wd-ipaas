package com.wakedata.dw.open.service.api.dto;

import com.alibaba.fastjson.annotation.JSONField;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 自定义函数
 * @author luomeng
 * @date 2022/11/3 14:20
 */
@Data
@ApiModel("自定义函数")
public class CustomFunctionDTO implements Serializable {

    @ApiModelProperty("主键id")
    private Long id;

    @ApiModelProperty("租户id")
    private Long lesseeId;

    @ApiModelProperty("函数名称(以字母开头，只能包含字母加数字，租户唯一)")
    private String funcName;

    @ApiModelProperty("函数代码编写语言，默认groovy")
    private String funcLanguage;

    @ApiModelProperty("函数说明")
    private String funcDesc;

    /**
     * {@link com.wakedata.dw.open.function.custom.CustomFunctionStatus.StatusEnum}
     */
    @ApiModelProperty("状态 1：草稿 2：上线 3：下线")
    private Integer status;

    @ApiModelProperty("函数参数 ,json数组串 [{name:参数名称,type:参数类型,desc:参数说明}]")
    private String funcParam;

    @ApiModelProperty("函数返回类型")
    private String funcReturn;

    @ApiModelProperty("函数代码")
    private String funcCode;

    /**
     * {@link com.wakedata.dw.open.function.custom.CustomFunctionStatus.DebugStatusEnum}
     */
    @ApiModelProperty("测试状态 1：未测试 2：测试通过 3：测试失败 （只有测试通过的函数才能上线）")
    private Integer debugStatus;

    @ApiModelProperty("创建人")
    private String createBy;

    @ApiModelProperty("更新人")
    private String updateBy;

    @ApiModelProperty("创建时间")
    @JSONField(format="yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    @ApiModelProperty("更新时间")
    @JSONField(format="yyyy-MM-dd HH:mm:ss")
    private Date updateTime;

}
