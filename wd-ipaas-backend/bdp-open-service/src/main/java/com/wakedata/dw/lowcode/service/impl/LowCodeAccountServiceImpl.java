package com.wakedata.dw.lowcode.service.impl;

import com.wakedata.dw.lowcode.mapper.LowCodeAccountMapper;
import com.wakedata.dw.lowcode.model.LowCodeAccountPo;
import com.wakedata.dw.lowcode.service.LowCodeAccountService;
import com.wakedata.dw.open.service.CurdService;
import com.wakedata.dw.open.service.impl.BaseServiceImpl;
import java.util.List;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

/**
 * @author wanghu@wakedata.com
 * @title 低代码应用绑定的账号
 * @date 2021/11/24
 * @since v1.0.0
 */
@Service
public class LowCodeAccountServiceImpl extends
    BaseServiceImpl<LowCodeAccountPo, LowCodeAccountMapper> implements LowCodeAccountService {

    @Autowired
    @Override
    protected void init(CurdService<LowCodeAccountPo> curdService, LowCodeAccountMapper mapper) {
        super.set(curdService, mapper);
    }

    @Override
    public List<LowCodeAccountPo> listByAppIds(Set<Integer> appIds) {
        Example example = new Example(LowCodeAccountPo.class);
        example.createCriteria().andIn("appId", appIds);

        return getMapper().selectByExample(example);
    }
}
