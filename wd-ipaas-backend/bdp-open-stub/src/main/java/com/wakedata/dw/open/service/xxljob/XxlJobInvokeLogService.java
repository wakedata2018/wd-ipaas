package com.wakedata.dw.open.service.xxljob;

import com.wakedata.dw.open.dto.PageResultDTO;
import com.wakedata.dw.open.model.query.XxlJobInvokeLogQuery;
import com.wakedata.dw.open.service.api.dto.XxlJobInvokeLogDTO;

import java.util.List;

/**
 * @author WangChenSheng
 * @descriptor
 * @title XxlJobInvokeLogService
 * @date 2022/11/2 17:13
 */
public interface XxlJobInvokeLogService {

    PageResultDTO<List<XxlJobInvokeLogDTO>> queryInvokeLogList(XxlJobInvokeLogQuery xxlJobInvokeLogQuery);

}
