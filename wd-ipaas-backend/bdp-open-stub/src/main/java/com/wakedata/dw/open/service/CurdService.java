package com.wakedata.dw.open.service;

import com.github.pagehelper.Page;
import com.wakedata.dw.open.model.BasePo;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;


/**
 * @author yiyufeng
 * @title CurdService
 * @projectName bdp-open
 * @date
 * @description
 */
public interface CurdService<T extends BasePo> {

    /**
     * 按主键查找实体类
     *
     * @param obj
     * @param mapper
     * @return
     */
    T selectByPrimaryKey(Object obj, Mapper<T> mapper);

    /**
     * 按实体类查找集合
     *
     * @param t
     * @param mapper
     * @return
     */
    List<T> select(T t, Mapper<T> mapper);

    /**
     * 实体插入
     *
     * @param t
     * @param mapper
     * @return
     */
    int insert(T t, Mapper<T> mapper);

    /**
     * 有条件插入
     *
     * @param t
     * @param mapper
     * @return
     */
    int insertSelective(T t, Mapper<T> mapper);

    /**
     * 根据主键更新
     *
     * @param t
     * @param mapper
     * @return
     */
    int updateByPrimaryKey(T t, Mapper<T> mapper);

    /**
     * 根据主键和其余条件更新
     *
     * @param t
     * @param mapper
     * @return
     */
    int updateByPrimaryKeySelective(T t, Mapper<T> mapper);

    /**
     * 根据主键删除
     *
     * @param obj
     * @param mapper
     * @return
     */
    int deleteByPrimaryKey(Object obj, Mapper<T> mapper);

    /**
     * 根据其他条件删除
     *
     * @param t
     * @param mapper
     * @return
     */
    int delete(T t, Mapper<T> mapper);

    /**
     * 分页查找
     * @param t
     * @param mapper
     * @param pageNo
     * @param pageSize
     * @return
     */
    Page<T> selectPageData(T t, Mapper<T> mapper, Integer pageNo, Integer pageSize);

    /**
     * 对PO类查询进行分页模糊，按字段排序，支持between
     *
     * @param t
     * @param mapper
     * @param pageNo
     * @param pageSize
     * @param like
     * @param likeColumns   属性名，一般是JAVA驼峰
     * @param orderBy       数据库字段名，一般是大写带下划线的列
     * @param asc
     * @param betweenColumn 属性名，一般是JAVA驼峰
     * @param from
     * @param to
     * @param inField in条件字段
     * @param ids in条件value列表
     * @return
     */
    Page<T> selectPageLikeOrderBy(T t, Mapper<T> mapper, Integer pageNo, Integer pageSize, String like,
                                  List<String> likeColumns, String orderBy, Boolean asc, String betweenColumn, Object from, Object to, String inField, List<Integer> ids);

    Page<T> selectPageLikeNotEqualOrderBy(T t, Mapper<T> mapper, Integer pageNo, Integer pageSize,
        String likeValue, List<String> likeColumns, String field,
        Boolean asc, String betweenColumn, Object from, Object to, String inField, List<Integer> ids, String notEqualColumn, List<Integer> notEqualValue, Integer publishStatus, Integer authId);
}
