package com.wakedata.dw.open.mapper.connector;

import com.wakedata.dw.open.model.connector.ConnectorParamsPo;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;
import tk.mybatis.mapper.common.special.InsertListMapper;

/**
 * @author zhengqinghui@wakedata.com
 * @date 2022/11/18 11:46
 */
@Repository
public interface ConnectorParamsMapper extends Mapper<ConnectorParamsPo>, InsertListMapper<ConnectorParamsPo> {
}
