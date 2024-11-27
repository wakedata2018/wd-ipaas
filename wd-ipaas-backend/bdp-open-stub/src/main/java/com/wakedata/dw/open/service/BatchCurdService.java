package com.wakedata.dw.open.service;

import com.wakedata.dw.open.model.BasePo;
import tk.mybatis.mapper.additional.idlist.SelectByIdListMapper;
import tk.mybatis.mapper.common.Mapper;
import tk.mybatis.mapper.common.special.InsertListMapper;

import java.util.List;

/**
 * @author yiyufeng
 * @title BatchCurdService
 * @projectName bdp-open
 * @date
 * @description
 */
public interface BatchCurdService<T extends BasePo> {

    /**
     * 批量插入
     *
     * @param insertValList
     * @param insertListMapper
     * @return
     */
    int insertList(List<? extends T> insertValList, InsertListMapper<T> insertListMapper);

    /**
     * 用主键批量查询
     *
     * @param list
     * @param selectByIdListMapper
     * @param <PK>
     * @return
     */
    <PK> List<T> selectByIdList(List<PK> list, SelectByIdListMapper<T, PK> selectByIdListMapper);

    /**
     * 批量更新
     *
     * @param updateList
     * @param updateListMapper
     * @return
     */
    int updateListByPrimaryKeySelective(List<? extends T> updateList, Mapper<T> updateListMapper);
}
