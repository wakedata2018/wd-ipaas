package com.wakedata.dw.open.mapper.swagger;

import com.wakedata.dw.open.model.swagger.SwaggerInfoPo;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;
import tk.mybatis.mapper.common.special.InsertListMapper;

/**
 * @author chenshaopeng
 * @date 2021/11/1
 */
@Repository
public interface SwaggerInfoMapper extends Mapper<SwaggerInfoPo>, InsertListMapper<SwaggerInfoPo> {

}
