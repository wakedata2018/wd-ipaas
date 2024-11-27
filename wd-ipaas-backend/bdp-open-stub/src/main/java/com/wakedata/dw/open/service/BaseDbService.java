package com.wakedata.dw.open.service;

import com.github.pagehelper.Page;
import com.wakedata.dw.open.model.BasePo;

import java.util.List;

/**
 * @author tanzhi
 * @title BaseDbService
 * @projectName bdp-open
 * @date 2019/9/9 17:35
 * @description
 */
public interface BaseDbService<T extends BasePo> {


    /**
     * 增加
     *
     * @param po
     * @return
     */
    Integer add(T po);

    /**
     * 删
     *
     * @param id
     * @return
     */
    Integer delete(Integer id);

    /**
     * 删除
     *
     * @param po
     * @return
     */
    Integer delete(T po);

    /**
     * 改
     *
     * @param po
     * @return
     */
    Integer update(T po);

    /**
     * 详情
     *
     * @param id
     * @return
     */
    T show(Integer id);

    /**
     * 列表
     *
     * @param po
     * @return
     */
    List<T> find(T po);

    /**
     * 分页
     *
     * @param po
     * @param pageNo
     * @param pageSize
     * @return
     */
    Page<T> find(T po, Integer pageNo, Integer pageSize);

    /**
     * 模糊查询
     *
     * @param clazz    映射的类
     * @param fields   要查询的字段
     * @param value    要查询的值，会自动在头尾加上百分比符号
     * @param pageNo
     * @param pageSize
     * @return
     */
    Page<T> like(Class<T> clazz, List<String> fields, String value, Integer pageNo, Integer pageSize);


    /**排序分页模糊
     * @param t
     * @param pageNo
     * @param pageSize
     * @param likeValue
     * @param likeColumns
     * @param field
     * @param asc
     * @param betweenColumn
     * @param from
     * @param to
     * @param field in字段
     * @param ids 列表
     * @return
     */
    Page<T> selectPageLikeOrderBy(T t, Integer pageNo, Integer pageSize,
                                  String likeValue, List<String> likeColumns, String field, Boolean asc, String betweenColumn, Object from, Object to, String inField, List<Integer> ids);

     Page<T> selectPageLikeNotEqualOrderBy(T t, Integer pageNo, Integer pageSize,
        String likeValue, List<String> likeColumns, String field, Boolean asc, String betweenColumn, Object from, Object to, String inField, List<Integer> ids,String notEqualColumn,List<Integer> notEqualValues,
     Integer publishStatus, Integer authId);
}
