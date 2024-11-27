package com.wakedata.dw.lowcode.service.impl;

import com.wakedata.dw.lowcode.mapper.LowCodeConfigMapper;
import com.wakedata.dw.lowcode.model.LowCodeConfigPo;
import com.wakedata.dw.lowcode.service.LowCodeConfigService;
import com.wakedata.dw.open.service.CurdService;
import com.wakedata.dw.open.service.impl.BaseServiceImpl;
import java.util.Objects;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 配置信息 - service实现
 *
 * @author chenshaopeng
 * @date 2021/11/24
 */
@Service
public class LowCodeConfigServiceImpl extends
    BaseServiceImpl<LowCodeConfigPo, LowCodeConfigMapper> implements LowCodeConfigService {

    @Autowired
    @Override
    protected void init(CurdService<LowCodeConfigPo> curdService, LowCodeConfigMapper mapper) {
        super.set(curdService, mapper);
    }

    @Override
    public Integer save(LowCodeConfigPo lowCodeConfigPo) {
        LowCodeConfigPo oldLowCodeConfigPo
            = findByAppIdType(lowCodeConfigPo.getAppId(), lowCodeConfigPo.getType());

        //新增
        if (Objects.isNull(oldLowCodeConfigPo)) {
            super.add(lowCodeConfigPo);
            return lowCodeConfigPo.getId();
        }
        //更新
        lowCodeConfigPo.setId(oldLowCodeConfigPo.getId());
        lowCodeConfigPo.setCreateTime(null);
        lowCodeConfigPo.setCreateBy(null);
        super.update(lowCodeConfigPo);
        return oldLowCodeConfigPo.getId();
    }

    @Override
    public LowCodeConfigPo findByAppIdType(Integer appId, String type) {
        LowCodeConfigPo queryParam = new LowCodeConfigPo();
        queryParam.setAppId(appId);
        queryParam.setType(type);
        return getMapper().selectOne(queryParam);
    }
}
