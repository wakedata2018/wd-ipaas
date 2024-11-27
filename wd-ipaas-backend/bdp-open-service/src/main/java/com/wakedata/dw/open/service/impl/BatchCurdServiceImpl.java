package com.wakedata.dw.open.service.impl;

import com.wakedata.dw.open.enums.MsgCodeEnum;
import com.wakedata.dw.open.exception.OpenException;
import com.wakedata.dw.open.model.BasePo;
import com.wakedata.dw.open.service.BatchCurdService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.additional.idlist.SelectByIdListMapper;
import tk.mybatis.mapper.common.Mapper;
import tk.mybatis.mapper.common.special.InsertListMapper;

import java.util.Date;
import java.util.List;

/**
 * @author yiyufeng
 * @title
 * @projectName bdp-open
 * @date
 * @description
 */
@Service
@Slf4j
public class BatchCurdServiceImpl<T extends BasePo> implements BatchCurdService<T> {

    @Override
    public int insertList(List<? extends T> insertValList, InsertListMapper<T> insertListMapper) {
        if (CollectionUtils.isEmpty(insertValList)) {
            return 0;
        }

        try {
            Date currentDate = new Date();
            for (T insertVal : insertValList) {
                insertVal.setCreateTime(currentDate);
                insertVal.setUpdateTime(currentDate);
            }
            log.info("插入数据库数据 {}", insertValList);
            int effectRecord = insertListMapper.insertList(insertValList);
            log.info("插入数据成功,影响行数 {}", effectRecord);
            return effectRecord;
        } catch (Exception e) {
            log.error("数据库插入数据发生错误 param {}", insertListMapper, e);
            throw new OpenException(MsgCodeEnum.w_dao_insert_error, e);
        }
    }

    @Override
    public <PK> List<T> selectByIdList(List<PK> list, SelectByIdListMapper<T, PK> selectByIdListMapper) {
        try {
            log.info("根据主键列表获取数据库数据 {}", list);
            return selectByIdListMapper.selectByIdList(list);
        } catch (Exception e) {
            log.error("根据主键列表获取数据库数据发生错误 param {}", list, e);
            throw new OpenException(MsgCodeEnum.w_dao_search_error, e);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateListByPrimaryKeySelective(List<? extends T> updateList, Mapper<T> updateListMapper) {
        if (CollectionUtils.isEmpty(updateList)) {
            return 0;
        }

        try {
            Date nowDate = new Date();
            int effectCount = 0;
            for (T t : updateList) {
                t.setUpdateTime(nowDate);
                effectCount += updateListMapper.updateByPrimaryKeySelective(t);
            }
            return effectCount;
        } catch (Exception e) {
            log.error("根据主键列表批量更新数据库数据发生错误 param {}", updateList, e);
            throw new OpenException(MsgCodeEnum.w_dao_update_error, e);
        }
    }
}
