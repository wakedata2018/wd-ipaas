package com.wakedata.openapi.sdk.generator.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author zhengqinghui@wakedata.com
 * @date 2022/8/22 18:03
 */
@Data
public class ApiGroupDTO implements Serializable {

    private static final long serialVersionUID = -1;

    /**
     * 默认级别
     */
    public static final int DEFAULT_LEVEL = 0;

    /**
     * 默认父ID
     */
    public static final int DEFAULT_PARENT_GROUP_ID = 0;

//    @ApiModelProperty("主键id")
    private Integer id;

//    @ApiModelProperty("接口分组名称")
    private String groupName;

//    @ApiModelProperty("接口分组编码")
    private String groupCode;

//    @ApiModelProperty("创建人")
    private String createUser;

//    @ApiModelProperty("接口分组公共路径")
    private String groupPath;

//    @ApiModelProperty("接口分组描述")
    private String groupDesc;

//    @ApiModelProperty("父id")
    private Integer parentId;

//    @ApiModelProperty("等级")
    private Integer level;

//    @ApiModelProperty(value = "api列表",hidden = true)
    private List<DataAssetApiDetailDTO> publishApiList;
}
