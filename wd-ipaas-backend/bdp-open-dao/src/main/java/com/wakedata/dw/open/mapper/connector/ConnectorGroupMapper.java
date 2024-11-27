package com.wakedata.dw.open.mapper.connector;

import com.wakedata.dw.open.model.connector.ConnectorGroupPo;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

/**
 * @author zhengqinghui@wakedata.com
 * @date 2022/11/17 12:05
 */
@Repository
public interface ConnectorGroupMapper extends Mapper<ConnectorGroupPo> {

    /**
     * 查出当前parentId下，排序字段最大的值
     */
    Integer queryParentIdMax(@Param("parentId") Long parentId);

    /**
     * 构建平台分类树
     */
    List<ConnectorGroupPo> getAllConnectorWithChildrenByParentId(@Param("parentId") Long parentId);
}
