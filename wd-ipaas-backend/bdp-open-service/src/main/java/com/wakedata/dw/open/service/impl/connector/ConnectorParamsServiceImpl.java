package com.wakedata.dw.open.service.impl.connector;

import cn.hutool.core.collection.CollectionUtil;
import com.wakedata.common.core.util.BeanUtil;
import com.wakedata.dw.open.dto.ResultDTO;
import com.wakedata.dw.open.enums.ConnectorApiAuthTypeEnum;
import com.wakedata.dw.open.mapper.connector.ConnectorParamsMapper;
import com.wakedata.dw.open.model.connector.ConnectorParamsPo;
import com.wakedata.dw.open.service.CurdService;
import com.wakedata.dw.open.service.connector.ConnectorParamsService;
import com.wakedata.dw.open.service.connector.dto.ConnectorParamsDTO;
import com.wakedata.dw.open.service.impl.BaseServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author zhengqinghui@wakedata.com
 * @date 2022/11/18 11:44
 */
@Service
public class ConnectorParamsServiceImpl extends BaseServiceImpl<ConnectorParamsPo, ConnectorParamsMapper> implements ConnectorParamsService {

    @Autowired
    private ConnectorParamsMapper connectorParamsMapper;

    @Autowired
    @Override
    protected void init(CurdService<ConnectorParamsPo> curdService, ConnectorParamsMapper mapper) {
        super.set(curdService, mapper);
    }

    @Override
    public ResultDTO<List<ConnectorParamsDTO>>  findParamsByConnectorId(Long connectorId) {
        Example example = new Example(ConnectorParamsPo.class);
        example.createCriteria()
                .andEqualTo("connectorId", connectorId);
        List<ConnectorParamsPo> connectorParamsPos = connectorParamsMapper.selectByExample(example);
        if (CollectionUtil.isEmpty(connectorParamsPos)){
            return new ResultDTO<>();
        }

        return ResultDTO.success(BeanUtil.copyList(connectorParamsPos,ConnectorParamsDTO.class));
    }

    @Override
    public ResultDTO<Map<String,ConnectorApiAuthTypeEnum>> queryAuthType() {
        ConnectorApiAuthTypeEnum[] values = ConnectorApiAuthTypeEnum.values();
        return ResultDTO.success(Arrays.stream(values).collect(Collectors.toMap(
                ConnectorApiAuthTypeEnum::getDesc, value -> value,
                (desc, connectorApiAuthTypeEnum) -> connectorApiAuthTypeEnum)));
    }
}
