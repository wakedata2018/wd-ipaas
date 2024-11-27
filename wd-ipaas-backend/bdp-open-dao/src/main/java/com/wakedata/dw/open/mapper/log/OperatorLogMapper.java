package com.wakedata.dw.open.mapper.log;

import com.wakedata.dw.open.model.log.OperatorLogPo;
import tk.mybatis.mapper.common.Mapper;

/**
 * @author tanzhi
 * @title OperatorLogMapper
 * @projectName bdp-open
 * @date 2019/10/9 17:50
 * @description 系统操作日志
 */
public interface OperatorLogMapper extends Mapper<OperatorLogPo> {

    /**
     * 定时清理SQL
     *
     * @return
     */
    int cleanUp();
}
