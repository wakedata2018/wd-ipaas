package com.wakedata.dw.open.service.connector.dto;

import com.alibaba.fastjson.annotation.JSONField;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * @author zhengqinghui@wakedata.com
 * @date 2022/11/17 10:40
 */
@Data
@ApiModel(value = "平台分类DTO", description = "平台分类DTO")
public class ConnectorGroupDTO implements Serializable {

    private static final long serialVersionUID = 0L;

    @ApiModelProperty("主键id")
    private Long id;

//    @ApiModelProperty("租户id")
//    private Long lesseeId;

    @NotNull(message = "parentId cannot be empty")
    @ApiModelProperty("父级id")
    private Long parentId;

    @ApiModelProperty("父级分类的名称")
    private String parentName;

    @NotBlank(message = "分类名称groupName不能为空")
    @ApiModelProperty("分类名称")
    private String groupName;

    @ApiModelProperty("描述")
    private String description;

    @ApiModelProperty("排序字段")
    private Integer sortField;

    @ApiModelProperty("子分类")
    private List<ConnectorGroupDTO> children;

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
