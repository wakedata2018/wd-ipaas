package com.wakedata.dw.open.service.connector;

import com.wakedata.dw.open.dto.PageResultDTO;
import com.wakedata.dw.open.dto.ResultDTO;
import com.wakedata.dw.open.model.connector.ConnectorAuthConfigPo;
import com.wakedata.dw.open.model.query.ConnectorAuthCofigPageQuery;
import com.wakedata.dw.open.service.BaseDbService;
import com.wakedata.dw.open.service.connector.dto.ConnectorAuthConfigDTO;

import java.util.List;

/**
 * @author luomeng
 * @date 2022/11/22 15:10
 */
public interface ConnectorAuthConfigService extends BaseDbService<ConnectorAuthConfigPo> {
    /**
     * 获取连接器鉴权配置
     * @param connectorAuthId
     * @return
     */
    ConnectorAuthConfigDTO getConnectorAuthConfigById(Long connectorAuthId);

    /**
     * 创建连接器鉴权配置
     * @param authConfigDTO
     * @return
     */
    ResultDTO<Long> create(ConnectorAuthConfigDTO authConfigDTO);

    /**
     * 更新连接器鉴权配置
     * @param authConfigDTO
     * @return
     */
    ResultDTO<Boolean> modify(ConnectorAuthConfigDTO authConfigDTO);

    /**
     * 删除连接器鉴权配置
     * @param lesseeId
     * @param id
     * @return
     */
    ResultDTO<Boolean> deleteAuthConfig(Long lesseeId,Long id);

    /**
     * 查询连接器鉴权配置
     * @param connectorPageQuery
     * @return
     */
    PageResultDTO<List<ConnectorAuthConfigDTO>> query(ConnectorAuthCofigPageQuery connectorPageQuery);

    /**
     * 获取租户下所有的连接器鉴权配置
     * @param lesseeId
     * @return
     */
    List<ConnectorAuthConfigDTO> getAllAuthConfig(Long lesseeId);
}
