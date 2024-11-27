package com.wakedata.dw.lowcode.service.impl;

import com.wakedata.dw.lowcode.mapper.LowCodePageDetailMapper;
import com.wakedata.dw.lowcode.mapper.LowCodePageSimpleMapper;
import com.wakedata.dw.lowcode.model.LowCodePageDetailPo;
import com.wakedata.dw.lowcode.model.LowCodePageSimplePo;
import com.wakedata.dw.lowcode.service.LowCodePageDetailService;
import com.wakedata.dw.lowcode.service.LowCodePageSimpleService;
import com.wakedata.dw.open.service.CurdService;
import com.wakedata.dw.open.service.impl.BaseServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 页面详情 - service实现
 *
 * @author chenshaopeng
 * @date 2021/11/24
 */
@Service
public class LowCodePageDetailServiceImpl extends
    BaseServiceImpl<LowCodePageDetailPo, LowCodePageDetailMapper> implements LowCodePageDetailService {

    @Autowired
    @Override
    protected void init(CurdService<LowCodePageDetailPo> curdService, LowCodePageDetailMapper mapper) {
         super.set(curdService, mapper);
    }


}
