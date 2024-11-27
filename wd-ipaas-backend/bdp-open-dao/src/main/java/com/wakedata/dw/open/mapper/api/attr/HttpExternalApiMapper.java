package com.wakedata.dw.open.mapper.api.attr;

import com.wakedata.dw.open.model.api.external.http.HttpExternalApiAttr;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

/**
 * @author tanzhi
 * @title ApiGroupMapper
 * @date 2019/11/27 11:02
 * @projectName bdp-open
 * @descriptor
 */
public interface HttpExternalApiMapper extends Mapper<HttpExternalApiAttr> {

    /**
     * 通过parentId,查父级信息
     *
     * @param apiId 父节点ID
     * @return 父节点信息
     */
    HttpExternalApiAttr getExternalApi(@Param("apiId") Integer apiId);

}
