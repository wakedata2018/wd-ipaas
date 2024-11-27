package com.wakedata.dw.open.mapper.swagger;

import com.wakedata.dw.open.model.query.SwaggerApiQuery;
import com.wakedata.dw.open.model.swagger.SwaggerApiDO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

/**
 * @author WangChenSheng
 * @descriptor
 * @title SwaggerApiMapper
 * @date 2022/8/24 13:38
 */
@Repository
public interface SwaggerApiMapper extends Mapper<SwaggerApiDO> {

    /**
     * 分页查询SwaggerApi
     *
     * @param query 查询条件
     * @return SwaggerApi列表
     */
    List<SwaggerApiDO> selectPageList(@Param("query") SwaggerApiQuery query);

    /**
     * 根据id更改导入状态
     *
     * @param swaggerApiDO swaggerApiDO
     * @return true/false
     */
    Boolean updateImportStatus(@Param("swaggerApiDO")SwaggerApiDO swaggerApiDO);

    /**
     * 批量插入
     *
     * @param swaggerApiDOList swaggerApiDOList
     */
    void batchInsert(@Param("swaggerApiDOList") List<SwaggerApiDO> swaggerApiDOList);
}
