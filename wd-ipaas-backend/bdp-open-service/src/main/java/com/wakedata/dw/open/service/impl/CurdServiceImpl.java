package com.wakedata.dw.open.service.impl;

import com.alibaba.fastjson.JSON;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.wakedata.dw.open.enums.MsgCodeEnum;
import com.wakedata.dw.open.exception.OpenException;
import com.wakedata.dw.open.model.BasePo;
import com.wakedata.dw.open.model.domain.ApiInfoDo;
import com.wakedata.dw.open.service.CurdService;
import com.wakedata.dw.open.service.auth.AuthInfoService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.common.Mapper;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collector;
import java.util.stream.Collectors;

/**
 * @author yiyufeng
 * @title CurdServiceImpl
 * @projectName bdp-open
 * @date
 * @description
 */
@Service
@Slf4j
public class CurdServiceImpl<T extends BasePo> implements CurdService<T> {

    @Resource
    private AuthInfoService authInfoService;

    @Override
    public List<T> select(T t, Mapper<T> mapper) {
        try {
            log.info("根据条件获取数据库数据 {}", t);
            return mapper.select(t);
        } catch (Exception e) {
            log.error("根据条件获取数据库数据发生错误 param {}", t, e);
            throw new OpenException(MsgCodeEnum.w_dao_search_error, e);
        }
    }

    @Override
    public T selectByPrimaryKey(Object obj, Mapper<T> mapper) {
        try {
            log.info("根据主键获取数据库数据 {}", obj);
            return mapper.selectByPrimaryKey(obj);
        } catch (Exception e) {
            log.error("根据主键获取数据库数据发生错误 param {}", obj, e);
            throw new OpenException(MsgCodeEnum.w_dao_search_error, e);
        }
    }

    @Override
    public int insert(T t, Mapper<T> mapper) {
        Date currentDate = new Date();
        t.setCreateTime(currentDate);
        t.setUpdateTime(currentDate);
        try {
            log.info("插入数据库数据 {}", t);
            int effectRecord = mapper.insert(t);
            log.info("插入数据成功,影响行数 {}", effectRecord);
            return effectRecord;
        } catch (Exception e) {
            log.error("数据库插入数据发生错误 param {}", t, e);
            throw new OpenException(MsgCodeEnum.w_dao_insert_error, e);
        }
    }

    @Override
    public int insertSelective(T t, Mapper<T> mapper) {
        Date currentDate = new Date();
        t.setCreateTime(currentDate);
        t.setUpdateTime(currentDate);
        try {
            log.info("插入数据库数据 {}", t);
            int effectRecord = mapper.insertSelective(t);
            log.info("插入数据成功,影响行数 {}", effectRecord);
            return effectRecord;
        } catch (Exception e) {
            log.error("数据库插入数据发生错误 param {}", t, e);
            throw new OpenException(MsgCodeEnum.w_dao_insert_error, e);
        }
    }

    @Override
    public int updateByPrimaryKey(T t, Mapper<T> mapper) {
        Date currentDate = new Date();
        t.setUpdateTime(currentDate);
        try {
            log.info("更新数据库数据 {}", t);
            int effectRecord = mapper.updateByPrimaryKey(t);
            log.info("更新数据库数据成功,影响行数 {}", effectRecord);
            return effectRecord;
        } catch (Exception e) {
            log.error("数据库更新数据发生错误 param {}", t, e);
            throw new OpenException(MsgCodeEnum.w_dao_update_error, e);
        }
    }

    @Override
    public int updateByPrimaryKeySelective(T t, Mapper<T> mapper) {
        Date currentDate = new Date();
        t.setUpdateTime(currentDate);
        try {
            log.info("更新数据库数据 {}", t);
            int effectRecord = mapper.updateByPrimaryKeySelective(t);
            log.info("更新数据库数据成功,影响行数 {}", effectRecord);
            return effectRecord;
        } catch (Exception e) {
            log.error("数据库更新数据发生错误 param {}", t, e);
            throw new OpenException(MsgCodeEnum.w_dao_update_error, e);
        }
    }

    @Override
    public int deleteByPrimaryKey(Object obj, Mapper<T> mapper) {
        try {
            log.info("删除数据库数据主键 {}", obj);
            int effectRecord = mapper.deleteByPrimaryKey(obj);
            log.info("删除数据库数据成功,影响行数 {}", effectRecord);
            return effectRecord;
        } catch (Exception e) {
            log.error("数据库删除主键数据发生错误 param {}", obj, e);
            throw new OpenException(MsgCodeEnum.w_dao_delete_primary_error, e);
        }
    }

    @Override
    public int delete(T t, Mapper<T> mapper) {
        try {
            log.info("根据条件删除数据库数据 {}", t);
            int effectRecord = mapper.delete(t);
            log.info("根据条件删除数据库数据成功,影响行数 {}", effectRecord);
            return effectRecord;
        } catch (Exception e) {
            log.error("数据库删除条件数据发生错误 param {}", t, e);
            throw new OpenException(MsgCodeEnum.w_dao_delete_condition_error, e);
        }
    }

    @Override
    public Page<T> selectPageData(T t, Mapper<T> mapper, Integer pageNo, Integer pageSize) {
        try {
            log.info("根据条件分页获取数据库数据 {}", t);
            PageHelper.startPage(pageNo, pageSize);
            Page<T> result = (Page<T>) mapper.select(t);
            return result;
        } catch (Exception e) {
            log.error("根据条件获取数据库数据发生错误 param {} page {} size{} ", t, pageNo, pageNo, e);
            throw new OpenException(MsgCodeEnum.w_dao_search_error, e);
        }
    }

    @Override
    public Page<T> selectPageLikeOrderBy(T t, Mapper<T> mapper, Integer pageNo, Integer pageSize,
                                         String likeValue, List<String> likeColumns, String field,
                                         Boolean asc, String betweenColumn, Object from, Object to, String inField, List<Integer> ids) {
        try {
            log.info("根据条件分页获取数据库数据，根据likeValue模糊，根据时间过滤，根据字段倒序 {}", t);
            Example example = Example.builder(t.getClass()).build();
            Map<String, Object> whereConditions = (Map<String, Object>) JSON.toJSON(t);
            Iterator<Map.Entry<String, Object>> iterator = whereConditions.entrySet().iterator();
            Example.Criteria criteria = example.createCriteria();
            Field[] declaredFields = t.getClass().getDeclaredFields();
            while (iterator.hasNext()) {
                Map.Entry<String, Object> next = iterator.next();
                String fieldName = next.getKey();
                Object value = next.getValue();
                if (value != null) {
                    boolean find = false;
                    for (Field declaredField : declaredFields) {
                        Class<?> type = declaredField.getType();
                        if (type.isEnum() && declaredField.getName().equals(fieldName)) {
                            Object[] enumConstants = type.getEnumConstants();
                            for (int i = 0; i < enumConstants.length; i++) {
                                if (enumConstants[i].toString().equals(value)) {
                                    example.and().andEqualTo(fieldName, i);
                                    find = true;
                                    break;
                                }
                            }
                        }
                    }
                    if (!find) {
                        example.and().andEqualTo(fieldName, value);
                    }
                }
            }
            if (StringUtils.isNotBlank(field)) {
                String orderBy = "  ";
                if (asc) {
                    orderBy += " " + field + " ";
                } else {
                    orderBy += " " + field + " DESC ";
                }
                example.setOrderByClause(orderBy);
            }
            if (!CollectionUtils.isEmpty(likeColumns) && StringUtils.isNotBlank(likeValue)) {
                Set<String> fieldSet = whereConditions.keySet();
                for (String likeColumn : likeColumns) {
                    if (likeColumn != null && fieldSet.contains(likeColumn)) {
                        criteria.orLike(likeColumn, '%' + likeValue + '%');
                    }
                }
            }

            if (StringUtils.isNotBlank(betweenColumn)) {
                example.and().andBetween(betweenColumn, from, to);
            }
            if (StringUtils.isNotBlank(inField) && CollectionUtils.isNotEmpty(ids)) {
                example.and().andIn(inField, ids);
            }
            if (pageNo != null && pageSize != null) {
                PageHelper.startPage(pageNo, pageSize);
                Page<T> result = (Page<T>) mapper.selectByExample(example);
                return result;
            } else {
                List<T> result = mapper.selectByExample(example);
                Page<T> page = new Page<>();
                page.addAll(result);
                return page;
            }
        } catch (
                Exception e) {
            log.error("根据条件分页排序获取数据库数据发生错误 param {} keyword {} page {} size{} field {} orderBy {} ",
                    t, likeValue, pageNo, pageNo, field, asc, e);
            throw new OpenException(MsgCodeEnum.w_dao_search_error, e);
        }
    }

    @Override
    public Page<T> selectPageLikeNotEqualOrderBy(T t, Mapper<T> mapper, Integer pageNo, Integer pageSize,
                                                 String likeValue, List<String> likeColumns, String field,
                                                 Boolean asc, String betweenColumn, Object from, Object to, String inField, List<Integer> ids, String notEqualColumn, List<Integer> notEqualValue, Integer publishStatus, Integer authId) {
        try {
            log.info("根据条件分页获取数据库数据，根据likeValue模糊，根据时间过滤，根据字段倒序 {}", t);
            Example example = Example.builder(t.getClass()).build();
            Map<String, Object> whereConditions = (Map<String, Object>) JSON.toJSON(t);
            Iterator<Map.Entry<String, Object>> iterator = whereConditions.entrySet().iterator();
            Example.Criteria criteria = example.createCriteria();
            Field[] declaredFields = t.getClass().getDeclaredFields();
            while (iterator.hasNext()) {
                Map.Entry<String, Object> next = iterator.next();
                String fieldName = next.getKey();
                Object value = next.getValue();
                if (value != null) {
                    boolean find = false;
                    for (Field declaredField : declaredFields) {
                        Class<?> type = declaredField.getType();
                        if (type.isEnum() && declaredField.getName().equals(fieldName)) {
                            Object[] enumConstants = type.getEnumConstants();
                            for (int i = 0; i < enumConstants.length; i++) {
                                if (enumConstants[i].toString().equals(value)) {
                                    example.and().andEqualTo(fieldName, i);
                                    find = true;
                                    break;
                                }
                            }
                        }
                    }
                    if (!find) {
                        example.and().andEqualTo(fieldName, value);
                    }
                }
            }

            if (StringUtils.isNotBlank(field)) {
                String orderBy = "  ";
                if (asc) {
                    orderBy += " " + field + " ";
                } else {
                    orderBy += " " + field + " DESC ";
                }
                example.setOrderByClause(orderBy);
            }
            if (!CollectionUtils.isEmpty(likeColumns) && StringUtils.isNotBlank(likeValue)) {
                Set<String> fieldSet = whereConditions.keySet();
                for (String likeColumn : likeColumns) {
                    if (likeColumn != null && fieldSet.contains(likeColumn)) {
                        criteria.orLike(likeColumn, '%' + likeValue + '%');
                    }
                }
            }

            if (publishStatus != null) {
                example.and().andEqualTo("dataAssetPublishStatus", publishStatus);
            }
            if (authId != null) {
                List<ApiInfoDo> apiInfoDoList = authInfoService.queryThirdPartyApi(authId);
                Optional.ofNullable(apiInfoDoList).ifPresent(doList -> {
                    List<Integer> apiIdList = doList.stream().map(ApiInfoDo::getApiId).collect(Collectors.toList());
                    example.and().andNotIn("dataAssetApiId", apiIdList);
                });
            }

            if (StringUtils.isNotBlank(notEqualColumn) && CollectionUtils.isNotEmpty(notEqualValue)) {
                example.and().andNotIn(notEqualColumn, notEqualValue);
            }

            if (StringUtils.isNotBlank(betweenColumn)) {
                example.and().andBetween(betweenColumn, from, to);
            }
            if (StringUtils.isNotBlank(inField) && CollectionUtils.isNotEmpty(ids)) {
                example.and().andIn(inField, ids);
            }
            if (pageNo != null && pageSize != null) {
                PageHelper.startPage(pageNo, pageSize);
                Page<T> result = (Page<T>) mapper.selectByExample(example);
                return result;
            } else {
                List<T> result = mapper.selectByExample(example);
                Page<T> page = new Page<>();
                page.addAll(result);
                return page;
            }
        } catch (
                Exception e) {
            log.error("根据条件分页排序获取数据库数据发生错误 param {} keyword {} page {} size{} field {} orderBy {} ",
                    t, likeValue, pageNo, pageNo, field, asc, e);
            throw new OpenException(MsgCodeEnum.w_dao_search_error, e);
        }
    }


}
