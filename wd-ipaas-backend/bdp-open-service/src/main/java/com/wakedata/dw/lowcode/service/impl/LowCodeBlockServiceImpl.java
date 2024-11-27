package com.wakedata.dw.lowcode.service.impl;

import com.wakedata.dw.open.service.CurdService;
import com.wakedata.dw.open.service.impl.BaseServiceImpl;
import com.wakedata.dw.lowcode.mapper.LowCodeBlockMapper;
import com.wakedata.dw.lowcode.model.LowCodeBlockPo;
import com.wakedata.dw.lowcode.service.LowCodeBlockService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author wanghu@wakedata.com
 * @title 低代码区块CRUD
 * @date 2021/11/24
 * @since v1.0.0
 */
@Service
public class LowCodeBlockServiceImpl extends
    BaseServiceImpl<LowCodeBlockPo, LowCodeBlockMapper> implements LowCodeBlockService {

    @Autowired
    @Override
    protected void init(CurdService<LowCodeBlockPo> curdService, LowCodeBlockMapper mapper) {
         super.set(curdService, mapper);
    }


}
