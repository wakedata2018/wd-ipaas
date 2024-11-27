package com.wakedata.dw.open.service.connector;

import com.wakedata.dw.open.dto.ResultDTO;
import com.wakedata.dw.open.enums.ConnectorApiAuthTypeEnum;
import com.wakedata.dw.open.model.connector.ConnectorParamsPo;
import com.wakedata.dw.open.service.BaseDbService;
import com.wakedata.dw.open.service.connector.dto.ConnectorParamsDTO;

import java.util.List;
import java.util.Map;

/**
 * @author zhengqinghui@wakedata.com
 * @date 2022/11/18 11:43
 */
public interface ConnectorParamsService extends BaseDbService<ConnectorParamsPo> {

    /**
     * 根据平台id查询平台鉴权字段
     *
     * @param connectorId 平台id
     * @return 平台的鉴权字段以及字段描述
     */
    ResultDTO<List<ConnectorParamsDTO>>  findParamsByConnectorId(Long connectorId);

    /**
     * 获取平台鉴权类型
     *
     * @return 鉴权类型列表
     */
    ResultDTO<Map<String,ConnectorApiAuthTypeEnum>> queryAuthType();
}
