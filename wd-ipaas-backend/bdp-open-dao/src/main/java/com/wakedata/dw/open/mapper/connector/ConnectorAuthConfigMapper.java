package com.wakedata.dw.open.mapper.connector;

import com.wakedata.dw.open.model.connector.ConnectorAuthConfigPo;
import com.wakedata.dw.open.model.query.ConnectorAuthCofigPageQuery;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;
import tk.mybatis.mapper.common.special.InsertListMapper;

import java.util.List;

/**
 * @author zhengqinghui@wakedata.com
 * @date 2022/11/21 10:56
 */
@Repository
public interface ConnectorAuthConfigMapper extends Mapper<ConnectorAuthConfigPo>, InsertListMapper<ConnectorAuthConfigPo> {

    /**
     * 查询列表
     * @param connectorPageQuery
     * @return
     */
    List<ConnectorAuthConfigPo> selectList(@Param("query") ConnectorAuthCofigPageQuery connectorPageQuery);

    /**
     * 查询详情
     * @param connectorAuthConfigId
     * @return
     */
    ConnectorAuthConfigPo selectDetail(@Param("connectorAuthConfigId") Long connectorAuthConfigId);
}
