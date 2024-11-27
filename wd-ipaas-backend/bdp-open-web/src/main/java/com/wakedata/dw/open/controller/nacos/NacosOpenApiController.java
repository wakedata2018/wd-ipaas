package com.wakedata.dw.open.controller.nacos;

import com.alibaba.cloud.nacos.NacosDiscoveryProperties;
import com.alibaba.nacos.api.naming.NamingService;
import com.alibaba.nacos.api.naming.pojo.Instance;
import com.wakedata.dw.open.dto.ResultDTO;
import com.wakedata.dw.open.enums.MsgCodeEnum;
import com.wakedata.dw.open.service.nacos.NacosOpenApiService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * nacos OpenApi控制层
 *
 * @author wujunqiang
 * @date 2021/12/13 21:15
 */
@Slf4j
@RestController
@RequestMapping("/nacos/api")
public class NacosOpenApiController {

    @Autowired
    private NacosOpenApiService nacosOpenApiService;

    @Autowired
    private NacosDiscoveryProperties discoveryProperties;

    /**
     * 获取服务列表
     *
     * @param pageNo   当前页
     * @param pageSize 每页数量
     * @return 服务列表
     */
    @RequestMapping("/getServiceList")
    public ResultDTO<String> getServiceList(Integer pageNo, Integer pageSize) {
        return nacosOpenApiService.getServiceList(pageNo, pageSize);
    }

    /**
     * 根据服务名获取元数据信息
     *
     * @param serviceName 服务名
     * @return 元数据信息
     */
    @RequestMapping("/getMetadataByServiceName")
    public ResultDTO<Map<String, String>> getMetadataByServiceName(String serviceName) {
        NamingService namingService = discoveryProperties.namingServiceInstance();
        try {
            Instance instance = namingService.getAllInstances(serviceName).get(0);
            return ResultDTO.success(instance.getMetadata());
        } catch (Exception e) {
            String errorMsg = String.format("获取%s元数据信息异常", serviceName);
            log.error(errorMsg, e);
            ResultDTO<Map<String, String>> dto = new ResultDTO<>();
            dto.setFailed(MsgCodeEnum.w_nacos_metadata_not_found);
            return dto;
        }
    }

}
