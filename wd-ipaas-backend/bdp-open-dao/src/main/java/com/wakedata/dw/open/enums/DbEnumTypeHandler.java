package com.wakedata.dw.open.enums;

import org.apache.ibatis.type.BaseDbEnum;
import org.apache.ibatis.type.CommonDbEnumValueHandler;
import org.apache.ibatis.type.MappedTypes;

/**
 * @author yiyufeng
 * @title DbEnumTypeHandler
 * @projectName bdp-open
 * @date 2019/9/5 11:55
 * @description
 */
@MappedTypes(basePackage = {"com.wakedata.dw.open.enums","com.wakedata.dw.open.datasource.enums"})
public class DbEnumTypeHandler<E extends BaseDbEnum> extends CommonDbEnumValueHandler<E> {
    public DbEnumTypeHandler(Class<E> type) {
        super(type);
    }
}
