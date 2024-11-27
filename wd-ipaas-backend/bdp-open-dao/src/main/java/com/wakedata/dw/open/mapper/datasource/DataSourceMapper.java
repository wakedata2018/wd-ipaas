package com.wakedata.dw.open.mapper.datasource;

import com.wakedata.dw.open.model.datasource.DataSourcePo;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

/**
 * @author tanzhi
 * @title DatasourceConfigMapper
 * @date 2019/11/18 14:24
 * @projectName bdp-open
 * @descriptor
 */
public interface DataSourceMapper extends Mapper<DataSourcePo> {
    /**
     * 数据源查询
     * @param dataSourcePo
     * @return
     */
    List<DataSourcePo> findByPo(@Param("dataSourcePo") DataSourcePo dataSourcePo);
}
