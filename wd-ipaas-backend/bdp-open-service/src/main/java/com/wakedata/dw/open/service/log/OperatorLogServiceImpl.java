package com.wakedata.dw.open.service.log;

import com.wakedata.dw.open.mapper.log.OperatorLogMapper;
import com.wakedata.dw.open.model.log.OperatorLogPo;
import com.wakedata.dw.open.service.CurdService;
import com.wakedata.dw.open.service.impl.BaseServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author tanzhi
 * @title OperatorLogServiceImpl
 * @date 2019/10/12 9:38
 * @projectName bdp-open
 * @descriptor
 */
@Service
public class OperatorLogServiceImpl extends BaseServiceImpl<OperatorLogPo, OperatorLogMapper> implements OperatorService {
    @Autowired
    @Override
    protected void init(CurdService<OperatorLogPo> curdService, OperatorLogMapper mapper) {
        super.set(curdService, mapper);
    }
}
