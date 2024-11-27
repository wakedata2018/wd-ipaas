package com.wakedata.dw.open.mapper.xxljob;

import com.wakedata.dw.open.model.query.XxlJobQuery;
import com.wakedata.dw.open.model.xxljob.XxlJobDO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

/**
 * @author WangChenSheng
 * @descriptor
 * @title XxlJobMapper
 * @date 2022/10/25 14:57
 */
@Repository
public interface XxlJobMapper extends Mapper<XxlJobDO> {

    /**
     * 条件查询XxlJob列表
     *
     * @param query 查询条件
     * @return XxlJob列表
     */
    List<XxlJobDO> selectPageList(@Param("query") XxlJobQuery query);

    /**
     * 新增XxlJob任务信息
     *
     * @param xxlJobDO 新增条件
     * @return true/false
     */
    int addXxlJob(@Param("xxlJobDO") XxlJobDO xxlJobDO);

}
