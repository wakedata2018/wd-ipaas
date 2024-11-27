package com.wakedata.dw.lowcode.mapper;

import com.wakedata.dw.lowcode.common.LowCodeBatchMapper;
import com.wakedata.dw.lowcode.model.LowCodeComponentPo;
import tk.mybatis.mapper.common.Mapper;

/**
 * 自定义组件(LowCodeComponentMapper)表数据库访问层
 *
 * @author wanghu
 * @since 2021-11-24 18:44:30
 */
public interface LowCodeComponentMapper extends Mapper<LowCodeComponentPo>, LowCodeBatchMapper {


}

