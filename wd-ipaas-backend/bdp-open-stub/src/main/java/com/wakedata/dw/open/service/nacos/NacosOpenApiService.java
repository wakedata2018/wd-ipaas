package com.wakedata.dw.open.service.nacos;

import com.wakedata.dw.open.dto.ResultDTO;

/**
 * nacos OpenApi业务接口
 *
 * @author wujunqiang
 * @date 2021/12/13 21:19
 */
public interface NacosOpenApiService {

    /**
     * 获取服务列表
     *
     * @param pageNo   当前页
     * @param pageSize 每页数量
     * @return 服务列表
     */
    ResultDTO<String> getServiceList(Integer pageNo, Integer pageSize);

}
