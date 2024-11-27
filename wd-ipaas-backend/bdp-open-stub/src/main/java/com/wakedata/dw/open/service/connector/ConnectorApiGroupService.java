package com.wakedata.dw.open.service.connector;

import com.wakedata.dw.open.dto.ResultDTO;
import com.wakedata.dw.open.model.connector.ConnectorApiGroupPo;
import com.wakedata.dw.open.service.BaseDbService;
import com.wakedata.dw.open.service.connector.dto.ConnectorApiGroupDTO;
import com.wakedata.dw.open.service.connector.dto.ConnectorApiGroupRelevanceDTO;

import java.util.List;

/**
 * @author zhengqinghui@wakedata.com
 * @date 2022/11/21 17:12
 */
public interface ConnectorApiGroupService extends BaseDbService<ConnectorApiGroupPo> {

    /**
     * 创建或更新api分组
     * @param connectorApiGroupDTO 连接器平台api分组DTO
     * @return Long 返回主键id
     */
    ResultDTO<Long> createOfModify(ConnectorApiGroupDTO connectorApiGroupDTO);

    /**
     * 删除api分组
     * @param id 连接器平台api分组主键id
     * @return Boolean 返回删除结果
     */
    ResultDTO<Boolean> delete(Long id);

    /**
     * 查询单个api分组（包含分组下的api）
     * @param id 连接器平台api分组主键id
     * @return ConnectorApiGroupDTO 返回api分组信息和分组下的api
     */
    ResultDTO<ConnectorApiGroupDTO> queryById(Long id);


    /**
     * 根据平台id，查询api分组列表（平台id为空时，就是查询全平台下的api分组列表）
     * @param connectorId 平台id
     * @return List<ConnectorApiGroupRelevanceDTO> 返回连接器平台以及api分组构成的树状结构
     */
    ResultDTO<List<ConnectorApiGroupRelevanceDTO>> queryByConnectorId(Long connectorId);

    /**
     * 根据连接器ID集合，查询连接器包含的API分组集合
     *
     * @param connectorIds 连接器ID集合
     * @return 连接器API分组集合
     */
    List<ConnectorApiGroupDTO> queryByConnectorIds(List<Long> connectorIds);

}
