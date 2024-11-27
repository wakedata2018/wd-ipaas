package com.wakedata.dw.open.model.api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * API节点信息
 *
 * @author wujunqiang
 * @date 2021/12/16 11:17
 */
@Data
@AllArgsConstructor
public class ApiInfoDTO {

    /**
     * API ID（DW_OPEN_API.DATA_ASSET_API_ID）
     */
    private Integer apiId;

    /**
     * API名称（如果是服务编排则显示编排中节点的步骤英文名）
     */
    private String name;

}
