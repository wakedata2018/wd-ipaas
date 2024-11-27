package com.wakedata.dw.open.service.impl.connector;

import cn.hutool.core.collection.CollectionUtil;
import com.wakedata.common.core.util.BeanUtil;
import com.wakedata.dw.open.dto.ResultDTO;
import com.wakedata.dw.open.mapper.connector.ConnectorEnvironmentAddressMapper;
import com.wakedata.dw.open.model.connector.ConnectorEnvironmentAddressPo;
import com.wakedata.dw.open.service.CurdService;
import com.wakedata.dw.open.service.connector.ConnectorEnvironmentAddressService;
import com.wakedata.dw.open.service.connector.dto.ConnectorEnvironmentAddressDTO;
import com.wakedata.dw.open.service.impl.BaseServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author zhengqinghui@wakedata.com
 * @date 2022/11/18 11:35
 */
@Service
public class ConnectorEnvironmentAddressServiceImpl extends BaseServiceImpl<ConnectorEnvironmentAddressPo, ConnectorEnvironmentAddressMapper> implements ConnectorEnvironmentAddressService {

    @Autowired
    @Override
    protected void init(CurdService<ConnectorEnvironmentAddressPo> curdService, ConnectorEnvironmentAddressMapper mapper) {
        super.set(curdService, mapper);
    }

    @Override
    public List<String> checkEnvironmentAddressName(Long connectorId, List<ConnectorEnvironmentAddressPo> environmentAddressPoList) {
        if (CollectionUtil.isEmpty(environmentAddressPoList)) {
            return new ArrayList<>();
        }
        List<ConnectorEnvironmentAddressPo> connectorEnvironmentAddressPoList = getMapper().checkEnvironmentAddressName(connectorId, environmentAddressPoList);
        return connectorEnvironmentAddressPoList.stream().map(ConnectorEnvironmentAddressPo::getAddressName).collect(Collectors.toList());
    }

    @Override
    public ResultDTO<List<ConnectorEnvironmentAddressDTO>> findByConnectorId(Long connectorId) {
        List<ConnectorEnvironmentAddressPo> connectorEnvironmentAddressPos = getMapper().selectByConnectorId(connectorId);
        return ResultDTO.success(BeanUtil.copyList(connectorEnvironmentAddressPos, ConnectorEnvironmentAddressDTO.class));
    }

}
