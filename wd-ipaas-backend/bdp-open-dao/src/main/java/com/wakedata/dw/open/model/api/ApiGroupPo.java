package com.wakedata.dw.open.model.api;

import com.wakedata.dw.open.model.BaseLesseePo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import javax.persistence.*;
import java.util.List;

/**
 * @author tanzhi
 * @title ApiGroupPo
 * @date 2019/11/27 10:57
 * @projectName bdp-open
 * @descriptor 接口分组信息
 */
@Table(name = "DW_OPEN_API_GROUP")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ApiModel("接口分组信息")
public class ApiGroupPo extends BaseLesseePo {

    /**
     * 默认级别
     */
    public static final int DEFAULT_LEVEL = 0;

    /**
     * 默认父ID
     */
    public static final int DEFAULT_PARENT_GROUP_ID = 0;

    @Column(name = "ID")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @ApiModelProperty("主键id")
    private Integer id;

    @Column(name = "GROUP_NAME")
    @ApiModelProperty("接口分组名称")
    private String groupName;

    @Column(name = "GROUP_CODE")
    @ApiModelProperty("接口分组编码")
    private String groupCode;

    @Column(name = "CREATE_USER")
    @ApiModelProperty("创建人")
    private String createUser;

    @Column(name = "GROUP_PATH")
    @ApiModelProperty("接口分组公共路径")
    private String groupPath;

    @Column(name = "GROUP_DESC")
    @ApiModelProperty("接口分组描述")
    private String groupDesc;

    @Column(name = "PARENT_ID")
    @ApiModelProperty("父id")
    private Integer parentId;

    @Column(name = "LEVEL")
    @ApiModelProperty("等级")
    private Integer level;

    @Transient
    private List<ApiGroupPo> children;

}
