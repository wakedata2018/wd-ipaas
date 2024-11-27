package com.wakedata.dw.open.model.domain;

import com.wakedata.dw.open.enums.DataAssetPublishStatusEnum;
import lombok.Data;

/**
 * @author tanzhi
 * @title ApiGroupVo
 * @date 2019/11/28 17:27
 * @projectName bdp-open
 * @descriptor
 */
@Data
public class ApiGroupDo {

    private Integer apiGroupId;
    private String groupName;
    private String groupPath;
    private Integer apiId;
    private String apiName;
    private DataAssetPublishStatusEnum publishStatusEnum;

}
