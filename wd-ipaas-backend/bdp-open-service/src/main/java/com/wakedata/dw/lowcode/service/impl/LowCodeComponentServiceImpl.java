package com.wakedata.dw.lowcode.service.impl;

import com.wakedata.dw.lowcode.common.LowCodeBatchMapper;
import com.wakedata.dw.lowcode.mapper.LowCodeComponentMapper;
import com.wakedata.dw.lowcode.model.LowCodeComponentPo;
import com.wakedata.dw.lowcode.service.LowCodeComponentService;
import com.wakedata.dw.open.enums.MsgCodeEnum;
import com.wakedata.dw.open.exception.OpenException;
import com.wakedata.dw.open.service.CurdService;
import java.util.List;
import java.util.Objects;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

/**
 * @author wanghu@wakedata.com
 * @title 低代码组件CRUD
 * @date 2021/11/24
 * @since v1.0.0
 */
@Service
public class LowCodeComponentServiceImpl extends
    AbstractLowCodeBatchService<LowCodeComponentPo, LowCodeComponentMapper> implements LowCodeComponentService {

    @Autowired
    @Override
    protected void init(CurdService<LowCodeComponentPo> curdService, LowCodeComponentMapper mapper) {
        super.set(curdService, mapper);
    }

    @Override
    public Boolean existsComponentName(String name, Integer id, Integer appId) {
        Example example = new Example(LowCodeComponentPo.class);
        example.createCriteria()
            .andEqualTo("appId", appId)
            .andEqualTo("name", name)
            .andNotEqualTo("id", id);
        return getMapper().selectCountByExample(example) > 0;
    }

    @Override
    public Integer add(LowCodeComponentPo basePo) {
        if (existsComponentName(basePo.getName(), null, basePo.getAppId())) {
            throw new OpenException("组件标识符已经存在");
        }
        return super.add(basePo);
    }

    @Override
    public Integer update(LowCodeComponentPo basePo) {
        if (Objects.isNull(basePo.getId())) {
            throw new OpenException(MsgCodeEnum.w_wrong_argument);
        }
        if (existsComponentName(basePo.getName(), basePo.getId(), basePo.getAppId())) {
            throw new OpenException("组件标识符已经存在");
        }
        return super.update(basePo);
    }

    @Override
    public LowCodeBatchMapper getBatchMapper() {
        return this.getMapper();
    }

    @Override
    public List<LowCodeComponentPo> findByAppId(Integer appId) {
        Example example = new Example(LowCodeComponentPo.class);
        example.selectProperties(
            "id", "updateTime", "createTime", "updateBy", "appId", "name", "title", "desc", "meta", "icon", "cover")
            .createCriteria().andEqualTo("appId", appId);
        return getMapper().selectByExample(example);
    }
}
