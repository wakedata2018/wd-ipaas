package com.wakedata.dw.open.mapper.connector;

import com.wakedata.dw.open.model.connector.ConnectorApiResponseParamPo;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;
import tk.mybatis.mapper.common.special.InsertListMapper;

/**
 * @author wujunqiang
 * @since 2022/11/21 19:17
 */
@Repository
public interface ConnectorApiResponseParamMapper extends Mapper<ConnectorApiResponseParamPo>, InsertListMapper<ConnectorApiResponseParamPo> {
}
