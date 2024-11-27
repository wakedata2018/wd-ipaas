package com.wakedata.dw.open.mapper.api.attr;

import com.wakedata.dw.open.model.api.rule.ApiRuleAttr;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;



/**
 * User: Administrator
 * Date: 2021/12/13
 * @author wangchensheng
 */
public interface ApiRuleAttrMapper extends Mapper<ApiRuleAttr> {
    /**
     * 插入单个规则
     * @param attr
     * @return
     */
    int insertRule(@Param("attr")ApiRuleAttr attr);

}
