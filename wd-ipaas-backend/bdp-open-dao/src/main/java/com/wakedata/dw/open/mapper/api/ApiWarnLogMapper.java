package com.wakedata.dw.open.mapper.api;

import com.wakedata.dw.open.model.warn.ApiWarnLogPo;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

/**
 * @author wq
 * @title ApiWarnLogMapper
 * @date 2020/10/9 17:26
 * @projectName dw-open
 * @description
 */
public interface ApiWarnLogMapper extends Mapper<ApiWarnLogPo> {
    Integer countWarnLogToday(@Param("dataAssetApiId") Integer dataAssetApiId,@Param("value") Integer value);
}
