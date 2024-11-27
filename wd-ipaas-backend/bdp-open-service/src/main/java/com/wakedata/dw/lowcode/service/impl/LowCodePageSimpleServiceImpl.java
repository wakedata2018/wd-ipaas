package com.wakedata.dw.lowcode.service.impl;

import com.github.pagehelper.Page;
import com.wakedata.dw.lowcode.common.LowCodeBatchMapper;
import com.wakedata.dw.lowcode.mapper.LowCodePageCategoryMapper;
import com.wakedata.dw.lowcode.mapper.LowCodePageDetailMapper;
import com.wakedata.dw.lowcode.mapper.LowCodePageSimpleMapper;
import com.wakedata.dw.lowcode.model.LowCodePageCategoryPo;
import com.wakedata.dw.lowcode.model.LowCodePageDetailPo;
import com.wakedata.dw.lowcode.model.LowCodePageSimplePo;
import com.wakedata.dw.lowcode.service.LowCodePageSimpleService;
import com.wakedata.dw.lowcode.service.dto.QueryLowCodePageDTO;
import com.wakedata.dw.open.enums.MsgCodeEnum;
import com.wakedata.dw.open.exception.OpenException;
import com.wakedata.dw.open.service.CurdService;
import java.util.Collections;
import java.util.Date;
import java.util.Objects;
import java.util.Optional;
import javax.annotation.Resource;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

/**
 * 页面基础信息 - service实现
 *
 * @author chenshaopeng
 * @date 2021/11/24
 */
@Service
public class LowCodePageSimpleServiceImpl extends
    AbstractLowCodeBatchService<LowCodePageSimplePo, LowCodePageSimpleMapper> implements LowCodePageSimpleService {

    @Autowired
    @Override
    protected void init(CurdService<LowCodePageSimplePo> curdService, LowCodePageSimpleMapper mapper) {
         super.set(curdService, mapper);
    }

    @Resource
    private LowCodePageDetailMapper lowCodePageDetailMapper;

    @Resource
    private LowCodePageCategoryMapper lowCodePageCategoryMapper;


    @Override
    @Transactional(rollbackFor = Exception.class)
    public Integer add(LowCodePageSimplePo simplePo, LowCodePageDetailPo detailPo) {
        LowCodePageSimplePo condition = new LowCodePageSimplePo();
        condition.setAppId(simplePo.getAppId());
        condition.setName(simplePo.getName());
        if(super.find(condition).size() > 0){
            throw new OpenException("页面名称已存在");
        }

        if(super.add(simplePo) < 1){
            throw new OpenException("新增页面错误");
        }
        detailPo.setSimpleId(simplePo.getId());
        detailPo.setCreateTime(simplePo.getCreateTime());
        detailPo.setUpdateTime(simplePo.getUpdateTime());
        detailPo.setCreateBy(simplePo.getCreateBy());
        lowCodePageDetailMapper.insert(detailPo);

        return simplePo.getId();
    }

    @Override
    public boolean update(LowCodePageSimplePo simplePo, LowCodePageDetailPo detailPo) {
        LowCodePageSimplePo simple = show(simplePo.getId());
        if(Objects.isNull(simple)){
            throw new RuntimeException("页面基础信息不存在");
        }
        simplePo.setCreateBy(simple.getCreateBy());
        simplePo.setCreateTime(simple.getCreateTime());

        if(Objects.nonNull(detailPo)){
            LowCodePageDetailPo condition = LowCodePageDetailPo.builder().simpleId(simplePo.getId()).build();
            condition = lowCodePageDetailMapper.selectOne(condition);
            if(Objects.isNull(condition)){
                throw new RuntimeException("页面详情信息不存在");
            }
            detailPo.setId(condition.getId());
            detailPo.setSimpleId(simplePo.getId());
            detailPo.setUpdateBy(simplePo.getUpdateBy());
            detailPo.setCreateTime(condition.getCreateTime());
            lowCodePageDetailMapper.updateByPrimaryKey(detailPo);
        }
        return super.update(simplePo) > 0;
    }

    @Override
    public boolean deleteById(Integer id) {
        return super.delete(id) + lowCodePageDetailMapper
                .delete(LowCodePageDetailPo.builder().simpleId(id).build()) > 0;
    }

    @Override
    public Page<LowCodePageSimplePo> selectPage(QueryLowCodePageDTO query) {
        LowCodePageSimplePo configInfo = new LowCodePageSimplePo();
        configInfo.setAppId(query.getAppId());
        if(Objects.nonNull(query.getCategoryId())){
            configInfo.setCategoryId(query.getCategoryId());
        }
        return super.selectPageLikeOrderBy(configInfo, query.getPageNo(), query.getPageSize(), query.getTitle()
                , Collections.singletonList("title"), null, false
                , null, null, null, null, null);
    }

    @Override
    public boolean setCategory(Integer id, Integer categoryId) {
        LowCodePageCategoryPo condition = new LowCodePageCategoryPo();
        condition.setId(categoryId);
        if(lowCodePageCategoryMapper.selectCount(condition) < 1){
            throw new RuntimeException("页面分类不存在");
        }

        LowCodePageSimplePo simple = show(id);
        if(Objects.isNull(simple)){
            throw new RuntimeException("页面基础信息不存在");
        }
        simple.setCategoryId(categoryId);
        return super.update(simple) > 0;
    }

    @Override
    public boolean copyPage(Integer id, String name) {
        LowCodePageSimplePo simple = show(id);
        if(Objects.isNull(simple)){
            throw new RuntimeException("页面基础信息不存在");
        }

        LowCodePageDetailPo detail = LowCodePageDetailPo.builder().simpleId(id).build();
        detail = lowCodePageDetailMapper.selectOne(detail);
        if(Objects.isNull(detail)){
            throw new RuntimeException("页面详情信息不存在");
        }
        // copy, 重置simple参数
        simple.setCreateTime(new Date());
        simple.setUpdateTime(simple.getUpdateTime());
        simple.setId(null);
        simple.setCategoryId(null);
        simple.setName(name);

        if(super.add(simple) < 1){
            return false;
        }

        // copy, 重置detail参数
        detail.setId(null);
        simple.setCategoryId(simple.getCategoryId());
        detail.setCreateTime(simple.getCreateTime());
        detail.setUpdateTime(detail.getCreateTime());
        detail.setSimpleId(simple.getId());

        return lowCodePageDetailMapper.insert(detail) > 0;
    }

    @Override
    public LowCodePageSimplePo detail(Integer simpleId) {
        LowCodePageSimplePo lowCodePageSimplePo = super.show(simpleId);

        Optional.ofNullable(lowCodePageSimplePo).ifPresent(item -> {
            LowCodePageDetailPo param = LowCodePageDetailPo.builder().simpleId(simpleId).build();
            LowCodePageDetailPo lowCodePageDetailPo = lowCodePageDetailMapper.selectOne(param);
            if (Objects.nonNull(lowCodePageDetailPo)) {
                lowCodePageSimplePo.setContent(lowCodePageDetailPo.getContent());
                lowCodePageSimplePo.setCompressedContent(lowCodePageDetailPo.getCompressedContent());
            }
        });

        return lowCodePageSimplePo;
    }

    @Override
    public Boolean existsPageName(String name, Integer id, Integer appId) {
        if (StringUtils.isBlank(name)) {
            throw new OpenException(MsgCodeEnum.w_wrong_argument);
        }
        Example example = new Example(LowCodePageSimplePo.class);
        example.createCriteria()
            .andEqualTo("appId", appId)
            .andEqualTo("name", name)
            .andNotEqualTo("id", id);
        return getMapper().selectCountByExample(example) > 0;
    }

    @Override
    public LowCodeBatchMapper getBatchMapper() {
        return lowCodePageDetailMapper;
    }
}
