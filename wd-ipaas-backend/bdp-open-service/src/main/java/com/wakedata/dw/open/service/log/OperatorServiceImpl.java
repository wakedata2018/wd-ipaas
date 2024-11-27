package com.wakedata.dw.open.service.log;

import com.wakedata.dw.open.mapper.log.OperatorLogMapper;
import com.wakedata.dw.open.model.log.OperatorLogPo;
import com.wakedata.dw.open.service.CurdService;
import com.wakedata.dw.open.service.impl.BaseServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author tanzhi
 * @title OperatorServiceImpl
 * @projectName bdp-open
 * @date 2019/10/9 20:59
 * @description 操作日志service类
 */
@Service
public class OperatorServiceImpl extends BaseServiceImpl<OperatorLogPo, OperatorLogMapper> implements OperatorLogService {
    @Autowired
    @Override
    protected void init(CurdService<OperatorLogPo> curdService, OperatorLogMapper mapper) {
        super.set(curdService, mapper);
    }
}
