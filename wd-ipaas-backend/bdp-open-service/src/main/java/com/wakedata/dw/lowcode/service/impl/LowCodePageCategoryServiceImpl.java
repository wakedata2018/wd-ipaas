package com.wakedata.dw.lowcode.service.impl;

import com.wakedata.dw.lowcode.mapper.LowCodePageCategoryMapper;
import com.wakedata.dw.lowcode.model.LowCodePageCategoryPo;
import com.wakedata.dw.lowcode.service.LowCodePageCategoryService;
import com.wakedata.dw.open.service.CurdService;
import com.wakedata.dw.open.service.impl.BaseServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 分类信息 - service实现
 *
 * @author chenshaopeng
 * @date 2021/11/24
 */
@Service
public class LowCodePageCategoryServiceImpl extends
    BaseServiceImpl<LowCodePageCategoryPo, LowCodePageCategoryMapper> implements LowCodePageCategoryService {

    @Autowired
    @Override
    protected void init(CurdService<LowCodePageCategoryPo> curdService, LowCodePageCategoryMapper mapper) {
         super.set(curdService, mapper);
    }


}
