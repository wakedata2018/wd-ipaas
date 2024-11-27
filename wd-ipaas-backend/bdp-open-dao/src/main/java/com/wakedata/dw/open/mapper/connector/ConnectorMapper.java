package com.wakedata.dw.open.mapper.connector;

import com.wakedata.dw.open.model.connector.ConnectorPo;
import com.wakedata.dw.open.model.query.ConnectorPageQuery;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

/**
 * @author zhengqinghui@wakedata.com
 * @date 2022/11/17 15:46
 */
@Repository
public interface ConnectorMapper extends Mapper<ConnectorPo> {


    /**
     * 查询连接器平台
     */
    List<ConnectorPo> pageQueryConnector(@Param("query")ConnectorPageQuery query);
}
