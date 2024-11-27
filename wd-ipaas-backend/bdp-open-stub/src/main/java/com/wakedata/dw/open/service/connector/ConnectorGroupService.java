package com.wakedata.dw.open.service.connector;

import com.wakedata.dw.open.dto.ResultDTO;
import com.wakedata.dw.open.model.connector.ConnectorGroupPo;
import com.wakedata.dw.open.service.BaseDbService;
import com.wakedata.dw.open.service.connector.dto.ConnectorGroupDTO;

import java.util.List;

/**
 * @author zhengqinghui@wakedata.com
 * @date 2022/11/17 11:55
 */
public interface ConnectorGroupService extends BaseDbService<ConnectorGroupPo> {

    /**
     * 新增/更新平台分类
     *
     * @param connectorGroupDTO 平台分类DTO
     * @return Long 主键id
     */
    ResultDTO<Long> addOfModifyConnectorGroup(ConnectorGroupDTO connectorGroupDTO);

    /**
     * 删除平台分类
     *
     * @param id 平台分类主键id
     * @return Boolean 是否成功
     */
    ResultDTO<Boolean> deleteConnectorGroup(Long id);

    /**
     * 查询平台分类（树）
     *
     * @param connectorGroupDTO 平台分类DTO
     * @return List<ConnectorGroupDTO> 平台分类树
     */
    ResultDTO<List<ConnectorGroupDTO>> queryConnectorGroup(ConnectorGroupDTO connectorGroupDTO);

    /**
     * 根据id查询连接器分类
     *
     * @param groupId 连接器分类id
     * @return 连接器分类DTO
     */
    ConnectorGroupDTO checkForExist(Long groupId);
}
