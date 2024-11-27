package com.wakedata.dw.open.service.connector;

import com.wakedata.dw.open.dto.ResultDTO;
import com.wakedata.dw.open.model.connector.ConnectorEnvironmentAddressPo;
import com.wakedata.dw.open.service.BaseDbService;
import com.wakedata.dw.open.service.connector.dto.ConnectorEnvironmentAddressDTO;

import java.util.List;

/**
 * @author zhengqinghui@wakedata.com
 * @date 2022/11/18 11:34
 */
public interface ConnectorEnvironmentAddressService extends BaseDbService<ConnectorEnvironmentAddressPo> {

    /**
     * 新增时，校验出传入的数据中，是否存在当前平台内重复的环境地址名称
     *
     * @param connectorId 平台id
     * @param environmentAddressPoList 环境地址PoList
     * @return List<String> 以存在的环境地址名称
     */
    List<String> checkEnvironmentAddressName(Long connectorId, List<ConnectorEnvironmentAddressPo> environmentAddressPoList);

    /**
     * 根据平台id查询平台环境地址集合
     *
     * @param connectorId 平台id
     * @return 平台环境地址集合
     */
    ResultDTO<List<ConnectorEnvironmentAddressDTO>> findByConnectorId(Long connectorId);
}
