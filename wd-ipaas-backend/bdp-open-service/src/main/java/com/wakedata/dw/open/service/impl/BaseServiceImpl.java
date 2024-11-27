package com.wakedata.dw.open.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.wakedata.dw.open.model.BasePo;
import com.wakedata.dw.open.service.CurdService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import tk.mybatis.mapper.common.Mapper;
import tk.mybatis.mapper.entity.Example;

import java.util.Date;
import java.util.List;

/**
 * @author tanzhi
 * @title BaseServiceImpl
 * @projectName bdp-open
 * @date 2019/9/9 17:43
 * @description 用抽象类适配常用service的增删改查功能
 */
public abstract class BaseServiceImpl<T extends BasePo, M extends Mapper<T>>  {

    /**
     * 加载时注入
     *
     * @param curdService
     * @param mapper
     */
    protected abstract void init(CurdService<T> curdService, M mapper);

    /**
     * 注入真正执行的类
     *
     * @param curdService
     * @param mapper
     */
    protected void set(CurdService<T> curdService, M mapper) {
        this.curdService = curdService;
        this.mapper = mapper;
    }

    private CurdService<T> curdService;
    private M mapper;


    /**
     * 增
     *
     * @param basePo
     * @return
     */
    public Integer add(T basePo) {
        Date updateTime = new Date();
        basePo.setUpdateTime(updateTime);
        basePo.setCreateTime(updateTime);
        return curdService.insert(basePo, mapper);
    }

    /**
     * 删
     *
     * @param id
     * @return
     */
    public Integer delete(Integer id) {
        return curdService.deleteByPrimaryKey(id, mapper);
    }

    /**
     * 删
     * @param po
     * @return
     */
    public Integer delete(T po) {
        return curdService.delete(po,mapper);
    }

    /**
     * 改
     *
     * @param basePo
     * @return
     */
    public Integer update(T basePo) {
        basePo.setUpdateTime(new Date());
        return curdService.updateByPrimaryKeySelective(basePo, mapper);
    }


    /**
     * 详情
     *
     * @param id
     * @return
     */
    public T show(Integer id) {
        T basePo = curdService.selectByPrimaryKey(id, mapper);
        return basePo;
    }


    /**
     * 列表
     *
     * @param basePo
     * @return
     */
    public List<T> find(T basePo) {
        return curdService.select(basePo, mapper);
    }

    /**
     * 分页
     *
     * @param basePo
     * @param pageNo
     * @param pageSize
     * @return
     */
    public Page<T> find(T basePo, Integer pageNo, Integer pageSize) {
        return curdService.selectPageData(basePo, mapper, pageNo, pageSize);
    }

    /**
     * 模糊查询
     *
     * @param clazz
     * @param fields
     * @param value
     * @return
     */
    public Page<T> like(Class<T> clazz, List<String> fields, String value, Integer pageNo, Integer pageSize) {
        Example example = Example.builder(clazz).build();
        if (StringUtils.isNotBlank(value)) {
            if (!CollectionUtils.isEmpty(fields)) {
                for (String field : fields) {
                    example.and().andLike(field, "%" + value + "%");
                }
            }
        }
        PageHelper.startPage(pageNo, pageSize);
        Page<T> ts = (Page<T>) mapper.selectByExample(example);
        return ts;
    }


    /**
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
     * @param inField
     * @param ids
     * @return
     */
    public Page<T> selectPageLikeOrderBy(T t, Integer pageNo, Integer pageSize,
                                         String likeValue, List<String> likeColumns, String field, Boolean asc, String betweenColumn, Object from, Object to, String inField, List<Integer> ids) {

        return this.curdService.selectPageLikeOrderBy(t, mapper, pageNo, pageSize, likeValue, likeColumns, field, asc, betweenColumn, from, to, inField ,ids);
    }

    public Page<T> selectPageLikeNotEqualOrderBy(T t, Integer pageNo, Integer pageSize,
        String likeValue, List<String> likeColumns, String field, Boolean asc, String betweenColumn, Object from, Object to, String inField, List<Integer> ids,String notEqualColumn,List<Integer> notEqualValues,
                                                 Integer publishStatus, Integer authId) {

        return this.curdService.selectPageLikeNotEqualOrderBy(t, mapper, pageNo, pageSize, likeValue, likeColumns, field, asc, betweenColumn, from, to, inField ,ids, notEqualColumn, notEqualValues, publishStatus, authId);
    }



    public CurdService<T> getCurdService() {
        return curdService;
    }

    public M getMapper() {
        return mapper;
    }




}
