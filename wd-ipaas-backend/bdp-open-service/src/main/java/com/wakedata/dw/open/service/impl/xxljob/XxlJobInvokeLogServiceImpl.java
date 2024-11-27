package com.wakedata.dw.open.service.impl.xxljob;

import com.wakedata.dw.open.dto.PageResultDTO;
import com.wakedata.dw.open.mapper.xxljob.XxlJobInvokeLogMapper;
import com.wakedata.dw.open.model.query.XxlJobInvokeLogQuery;
import com.wakedata.dw.open.model.xxljob.XxlJobInvokeLogDO;
import com.wakedata.dw.open.service.CurdService;
import com.wakedata.dw.open.service.api.dto.XxlJobInvokeLogDTO;
import com.wakedata.dw.open.service.impl.BaseServiceImpl;
import com.wakedata.dw.open.service.xxljob.XxlJobInvokeLogService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author WangChenSheng
 * @descriptor
 * @title XxlJobInvokeLogServiceImpl
 * @date 2022/11/2 17:21
 */
@Service
public class XxlJobInvokeLogServiceImpl extends BaseServiceImpl<XxlJobInvokeLogDO, XxlJobInvokeLogMapper> implements XxlJobInvokeLogService {
    @Override
    protected void init(CurdService<XxlJobInvokeLogDO> curdService, XxlJobInvokeLogMapper mapper) {
        super.set(curdService, mapper);
    }

    @Override
    public PageResultDTO<List<XxlJobInvokeLogDTO>> queryInvokeLogList(XxlJobInvokeLogQuery xxlJobInvokeLogQuery) {
        return null;
    }
}
