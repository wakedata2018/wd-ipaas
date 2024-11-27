package com.wakedata.dw.lowcode.common;

import com.wakedata.dw.lowcode.model.AppPo;
import java.util.List;
import java.util.Set;
import org.apache.ibatis.annotations.Param;

/**
 * 根据名称和应用查询低代码信息
 *
 * @author wanghu@wakedata.com
 * @date 2021/12/1
 * @since v1.0.0
 */
public interface LowCodeBatchMapper {

    /**
     * 根据名称和应用查询低代码信息
     */
    <R extends AppPo & UniqueId> List<R> listByBatch(
        @Param("names") Set<String> names, @Param("appId") Integer appId);
}
