package com.wakedata.dw.open.mapper.connector;

import com.wakedata.dw.open.model.connector.ConnectorApiPo;
import com.wakedata.dw.open.model.query.ConnectorApiPageQuery;
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
public interface ConnectorApiMapper extends Mapper<ConnectorApiPo>, InsertListMapper<ConnectorApiPo> {

    /**
     * 根据查询条件查询连接器api
     *
     * @param connectorApiPageQuery 查询条件
     * @return List<ConnectorApiPo> 连接器api列表
     */
    List<ConnectorApiPo> query(@Param("query") ConnectorApiPageQuery connectorApiPageQuery);
}
