package com.wakedata.dw.lowcode.service;

import com.github.pagehelper.Page;
import com.wakedata.dw.lowcode.model.LowCodePageDetailPo;
import com.wakedata.dw.lowcode.model.LowCodePageSimplePo;
import com.wakedata.dw.lowcode.service.dto.QueryLowCodePageDTO;
import com.wakedata.dw.open.service.BaseDbService;

/**
 * 页面基础信息 - service
 *
 * @author chenshaopeng
 * @date 2021/11/24
 */
public interface LowCodePageSimpleService extends BaseDbService<LowCodePageSimplePo>, LowCodeBatchService{

    /**
     * 新增页面
     *
     * @param simplePo 基础信息
     * @param detailPo 详情信息
     * @return 新增结果
     */
    Integer add(LowCodePageSimplePo simplePo , LowCodePageDetailPo detailPo);

    /**
     * 修改页面
     *
     * @param simplePo 基础信息
     * @param detailPo 详情信息
     * @return 新增结果
     */
    boolean update(LowCodePageSimplePo simplePo , LowCodePageDetailPo detailPo);

    /**
     * 删除页面
     *
     * @param id 页面id
     * @return 新增结果
     */
    boolean deleteById(Integer id);

    /**
     * 分页查询页面列表
     *
     * @param query 查询对象
     * @return 分页列表
     */
    Page<LowCodePageSimplePo> selectPage(QueryLowCodePageDTO query);

    /**
     * 设置页面分类
     *
     * @param id 页面Id
     * @param categoryId 分类Id
     * @return 更新结果
     */
    boolean setCategory(Integer id, Integer categoryId);

    /**
     * 拷贝页面
     *
     * @param id 被拷贝Id
     * @param name 新页面名称
     * @return 拷贝结果
     */
    boolean copyPage(Integer id, String name);

    /**
     * 查询页面详情
     *
     * @param simpleId 页面Id
     * @return 页面详情
     */
    LowCodePageSimplePo detail(Integer simpleId);


    /**
     * 是否存在页面名称
     *
     * @param name  标识符（名称）
     * @param id    主键
     * @param appId 应用id
     * @return 存在返回true
     */
    Boolean existsPageName(String name, Integer id, Integer appId);

}
