package com.wakedata.dw.open.mapper.api;

import com.wakedata.dw.open.model.api.ApiResponseParamPo;
import tk.mybatis.mapper.common.Mapper;
import tk.mybatis.mapper.common.special.InsertListMapper;

/**
 * API响应参数(DwOpenApiResponseParam)表数据库访问层
 *
 * @author wanghu
 * @since 2021-12-10 10:16:17
 */
public interface ApiResponseParamMapper extends Mapper<ApiResponseParamPo>, InsertListMapper<ApiResponseParamPo> {

}

