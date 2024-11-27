package com.wakedata.dw.open.service.connector;

import com.wakedata.dw.open.dto.PageResultDTO;
import com.wakedata.dw.open.dto.ResultDTO;
import com.wakedata.dw.open.model.query.ConnectorSecretKeyPageQuery;
import com.wakedata.dw.open.service.connector.dto.ConnectorSecretKeyDTO;

import java.util.List;

/**
 * @author wujunqiang
 * @since 2022/11/21 10:20
 */
public interface ConnectorSecretKeyService {

    /**
     * 根据租户id、平台id查询平台密钥列表
     *
     * @param lesseeId    租户id
     * @param connectorId 平台id
     * @return 平台密钥列表
     */
    ResultDTO<List<ConnectorSecretKeyDTO>> findByConnectorId(Long lesseeId, Long connectorId);

    /**
     * 新增平台密钥
     *
     * @param connectorSecretKeyDTO 平台密钥DTO
     * @return 平台密钥主键
     */
    ResultDTO<Long> create(ConnectorSecretKeyDTO connectorSecretKeyDTO);

    /**
     * 修改平台密钥
     *
     * @param connectorSecretKeyDTO 平台密钥DTO
     * @return Boolean
     */
    ResultDTO<Boolean> update(ConnectorSecretKeyDTO connectorSecretKeyDTO);

    /**
     * 根据id删除平台密钥
     *
     * @param id 平台密钥ID
     * @return Boolean
     */
    ResultDTO<Boolean> delete(Long id);

    /**
     * 分页查询平台密钥数据
     *
     * @param pageQuery 平台密钥查询条件
     * @return 平台密钥列表
     */
    PageResultDTO<List<ConnectorSecretKeyDTO>> page(ConnectorSecretKeyPageQuery pageQuery);
}
