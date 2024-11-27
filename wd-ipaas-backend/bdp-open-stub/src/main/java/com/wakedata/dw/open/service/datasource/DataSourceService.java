package com.wakedata.dw.open.service.datasource;

import com.wakedata.dw.open.model.datasource.DataSourcePo;
import com.wakedata.dw.open.service.BaseDbService;

import java.util.List;

/**
 * @author tanzhi
 * @title DataSourceConfigService
 * @date 2019/11/18 16:29
 * @projectName bdp-open
 * @descriptor
 */
public interface DataSourceService extends BaseDbService<DataSourcePo> {

    /**
     * 测试连通性
     *
     * @param dataSourcePo
     * @return
     */
    boolean test(DataSourcePo dataSourcePo);

    /**
     * 添加数据源
     * @param dataSourcePo
     * @return
     */
    Integer addDataSource(DataSourcePo dataSourcePo);

    /**
     * 修改数据源
     * @param dataSourcePo
     * @return
     */
    Integer updateDataSource(DataSourcePo dataSourcePo);

    /**
     * 删除数据源
     * 删除数据源
     * @param dataSourceId
     * @return
     */
    boolean deleteDataSource(Integer dataSourceId);


    /**
     * 数据源查询
     * @param dataSourcePo
     * @return
     */
    List<DataSourcePo> findByPo(DataSourcePo dataSourcePo);
}
