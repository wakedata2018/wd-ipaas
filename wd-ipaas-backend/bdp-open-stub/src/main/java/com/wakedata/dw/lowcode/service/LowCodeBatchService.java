package com.wakedata.dw.lowcode.service;

import com.wakedata.dw.lowcode.common.UniqueId;
import com.wakedata.dw.lowcode.model.AppPo;
import com.wakedata.dw.lowcode.service.dto.BatchFetchLowCodeInfoDTO;
import java.util.List;

/**
 * @author wanghu@wakedata.com
 * @title 低代码批量服务
 * @date 2021/12/1
 * @since v1.0.0
 */
public interface LowCodeBatchService {

    /**
     * 批量查询低代码信息
     *
     * @param dtoList 参数
     * @param appId   应用id
     * @param <T>     类型
     * @return po
     */
    <T extends AppPo & UniqueId> List<T> listByBatch(List<BatchFetchLowCodeInfoDTO> dtoList, Integer appId);
}
