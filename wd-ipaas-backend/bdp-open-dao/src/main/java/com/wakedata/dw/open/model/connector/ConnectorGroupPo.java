package com.wakedata.dw.open.model.connector;

import com.wakedata.dw.open.model.BaseLesseePo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import java.util.List;

/**
 * @author zhengqinghui@wakedata.com
 * @date 2022/11/16 16:02
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Table(name = "dw_open_connector_group")
@ApiModel(value = "平台分类表")
public class ConnectorGroupPo extends BaseLesseePo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ApiModelProperty("父级id")
    @Column(name = "parent_id")
    private Long parentId;

    @ApiModelProperty("分类名称")
    @Column(name = "group_name")
    private String groupName;

    @ApiModelProperty("描述")
    @Column(name = "description")
    private String description;

    @ApiModelProperty("排序字段")
    @Column(name = "sort_field")
    private Integer sortField;

    @ApiModelProperty("创建人")
    @Column(name = "create_by")
    private String createBy;

    @ApiModelProperty("更新人")
    @Column(name = "update_by")
    private String updateBy;

    @ApiModelProperty("子分类")
    @Transient
    private List<ConnectorGroupPo> children;


}
